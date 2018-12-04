package interfaceScala

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import chat.tox.antox.data.State
import chat.tox.antox.tox.ToxSingleton
import chat.tox.antox.toxme.ToxMe
import chat.tox.antox.utils.ProxyUtils
import chat.tox.antox.wrapper.ToxAddress
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.exceptions.ToxException
import rx.lang.scala.Subscription
import rx.lang.scala.schedulers.{AndroidMainThreadScheduler, IOScheduler}

object InterfaceScaleUtil {

  var _originalUsername: String = ""
  var lookupSubscription: Option[Subscription] = None
  var toast: Toast = _
  var context: Context = _

  def addFriend(friendID: String, contextMain: Context) {
    context = contextMain;
    if (friendID.length == 76) {
      // Attempt to use ID as a Tox ID
      val result = checkAndSend(friendID, _originalUsername)
      if (result) {

      }
    } else {
      // Attempt to use ID as a toxme account name
      _originalUsername = friendID
      try {
        val preferences = PreferenceManager.getDefaultSharedPreferences(contextMain)
        val proxy = ProxyUtils.netProxyFromPreferences(preferences)
        lookupSubscription = Some(
          ToxMe.lookup(_originalUsername, proxy)
            .subscribeOn(IOScheduler())
            .observeOn(AndroidMainThreadScheduler())
            .subscribe((m_key: Option[String]) => {
              m_key match {
                case Some(key) =>
                  val result = checkAndSend(key, _originalUsername)
                  if (result) {

                  }
                //case None => showToastInvalidID()
              }
            }))
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }
  }

  private def checkAndSend(rawAddress: String, originalUsername: String): Boolean = {
    if (ToxAddress.isAddressValid(rawAddress)) {
      val address = new ToxAddress(rawAddress)

      if (!isAddressOwn(address)) {
        val key = address.key
        var message = "Hey! Please accept my friend request"
        val alias = "PNR_IM_SERVER"

        val db = State.db
        if (!db.doesContactExist(key)) {
          try {
            ToxSingleton.tox.addFriend(address, ToxFriendRequestMessage.unsafeFromValue(message.getBytes))
            ToxSingleton.save()
          } catch {
            case e: ToxException[_] => e.printStackTrace()
          }
          db.addFriend(key, originalUsername, alias, "Friend Request Sent")

          //prevent already-added friend from having an existing friend request
          db.deleteFriendRequest(key)
          //AntoxNotificationManager.clearRequestNotification(key)
        } else {
          /*toast = Toast.makeText(context, getResources.getString(R.string.addfriend_friend_exists), Toast.LENGTH_SHORT)
          toast.show()*/
        }
      /*  toast = Toast.makeText(context, text, duration)
        toast.show()*/
        true
      } else {

        /*toast = Toast.makeText(context, getResources.getString(R.string.addfriend_own_key), Toast.LENGTH_SHORT)
        toast.show()*/
        false
      }
    } else {
      //showToastInvalidID()
      false
    }
  }

  private def isAddressOwn(address: ToxAddress): Boolean = {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val ownAddress = ToxAddress.removePrefix(preferences.getString("tox_id", ""))
    new ToxAddress(ownAddress) == address
  }
}