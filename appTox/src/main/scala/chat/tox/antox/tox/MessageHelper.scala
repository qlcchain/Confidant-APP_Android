package chat.tox.antox.tox

import android.content.Context
import chat.tox.antox.activities.{ChatActivity, GroupChatActivity}
import chat.tox.antox.data.State
import chat.tox.antox.utils._
import chat.tox.antox.wrapper._
import events.{ToxMessageEvent, ToxSendInfoEvent}
import im.tox.tox4j.core.data.{ToxFileId, ToxFriendMessage, ToxNickname}
import im.tox.tox4j.core.enums.ToxMessageType
import org.greenrobot.eventbus.EventBus
import org.scaloid.common.LoggerTag
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object MessageHelper {

  val TAG = LoggerTag("MessageHelper")
  def handleMessage(ctx: Context, friendInfo: FriendInfo, message: ToxFriendMessage, messageType: ToxMessageType): Unit = {
    val db = State.db

    AntoxLog.debug(s"Message from: friend id: ${friendInfo.key} activeKey: ${State.activeKey} chatActive: ${State.chatActive}", TAG)

    if (!db.isContactBlocked(friendInfo.key)) {
      val chatActive = State.isChatActive(friendInfo.key)
      db.addMessage(friendInfo.key, friendInfo.key, ToxNickname.unsafeFromValue(friendInfo.getDisplayName.getBytes), new String(message.value), hasBeenReceived = true,
        hasBeenRead = chatActive, successfullySent = true, messageType)
      EventBus.getDefault().post(new ToxMessageEvent(new String(message.value),friendInfo.key.toString()))
      if (!chatActive) {
        val unreadCount = db.getUnreadCounts(friendInfo.key)
        //AntoxNotificationManager.createMessageNotification(ctx, classOf[ChatActivity], friendInfo, new String(message.value), unreadCount)
      }
    }
  }

  def handleGroupMessage(ctx: Context, groupInfo: GroupInfo, peerInfo: GroupPeer, message: String, messageType: ToxMessageType): Unit = {
    val db = State.db

    val chatActive = State.isChatActive(groupInfo.key)

    db.addMessage(groupInfo.key, peerInfo.key, peerInfo.name, message, hasBeenReceived = true,
      hasBeenRead = chatActive, successfullySent = true, messageType)

    if (!chatActive) {
      //AntoxNotificationManager.createMessageNotification(ctx, classOf[GroupChatActivity], groupInfo, message)
    }
  }
  def getFriendOnline(ctx: Context, friendKey: FriendKey): Boolean =
  {
    val friendInfo = State.db.getFriendInfo(friendKey)
    return friendInfo.online
  }
  /**
    * 专门为kotlin调用的发送接口
    * @param ctx
    * @param friendKey
    * @param msg
    * @param messageType
    */
  def sendMessageFromKotlin(ctx: Context, friendKey: FriendKey, msg: String, messageType: ToxMessageType): Unit = {
    if(msg.indexOf("HeartBeat") <0)
    {

    }
    AntoxLog.debug("SendMessage:"+msg)
    State.setLastIncomingMessageAction()
    val db = State.db
    var mDbIdd: Option[Long] = None
    for (splitMsg <- splitMessage(msg)) {
      val databaseMessageId: Long = mDbIdd match {
        case Some(dbId) => dbId
        case None => {
          val senderKey = ToxSingleton.tox.getSelfKey
          val senderName = ToxSingleton.tox.getName
          db.addMessage(friendKey, senderKey, senderName, splitMsg, hasBeenReceived = false,
            hasBeenRead = false, successfullySent = false, messageType)
        }
      }
      Observable[Boolean](subscriber => {
        val mId = Try(ToxSingleton.tox.friendSendMessage(friendKey, ToxFriendMessage.unsafeFromValue(msg.getBytes), messageType)).toOption
        mId match {
          case Some(id) => db.updateUnsentMessage(id, databaseMessageId)
          case None => AntoxLog.debug(s"SendMessage failed. dbId = $databaseMessageId")
        }
        subscriber.onCompleted()
      }).subscribeOn(IOScheduler()).subscribe()
    }
  }
  def sendAgreeReceiveFileFromKotlin(context: Context,fileNumber: Int, key: FriendKey): Unit = {
    State.transfers.acceptFile(key, fileNumber, context)
  }
  /**
    * 发送文件
    * @param filePath
    * @param key
    * @param context
    */
  def sendFileSendRequestFromKotlin( context: Context,filePath: String, key: FriendKey): String = {
    var fileNumber:Int = State.transfers.sendFileSendRequest(filePath, key, FileKind.DATA, ToxFileId.empty, context)
    return fileNumber.toString()
  }
  def sendMessage(ctx: Context, friendKey: FriendKey, msg: String, messageType: ToxMessageType, mDbId: Option[Long]): Unit = {
    State.setLastIncomingMessageAction()
    val db = State.db
    for (splitMsg <- splitMessage(msg)) {
      val databaseMessageId: Long = mDbId match {
        case Some(dbId) => dbId
        case None => {
          val senderKey = ToxSingleton.tox.getSelfKey
          val senderName = ToxSingleton.tox.getName
          db.addMessage(friendKey, senderKey, senderName, splitMsg, hasBeenReceived = false,
            hasBeenRead = false, successfullySent = false, messageType)
        }
      }

      Observable[Boolean](subscriber => {
        val mId = Try(ToxSingleton.tox.friendSendMessage(friendKey, ToxFriendMessage.unsafeFromValue(msg.getBytes), messageType)).toOption

        mId match {
          case Some(id) => db.updateUnsentMessage(id, databaseMessageId)
          case None => AntoxLog.debug(s"SendMessage failed. dbId = $databaseMessageId")
        }
        subscriber.onCompleted()
      }).subscribeOn(IOScheduler()).subscribe()
    }
  }

  def sendGroupMessage(ctx: Context, groupKey: GroupKey, msg: String, messageType: ToxMessageType, mDbId: Option[Int]): Unit = {
    val db = State.db

    for (splitMsg <- splitMessage(msg)) {
      messageType match {
        case ToxMessageType.ACTION =>
          ToxSingleton.tox.sendGroupAction(groupKey, msg)
        case _ =>
          ToxSingleton.tox.sendGroupMessage(groupKey, msg)
      }

      val senderKey = ToxSingleton.tox.getSelfKey
      val senderName = ToxSingleton.tox.getName
      mDbId match {
        case Some(dbId) => db.updateUnsentMessage(0, dbId)
        case None => db.addMessage(groupKey, senderKey, senderName,
          splitMsg, hasBeenReceived =
            true, hasBeenRead = true, successfullySent = true, messageType)
      }
    }
  }

  def splitMessage(msg: String): Array[String] = {
    var currSplitPos = 0
    // convert the string to bytes to handle multibyte languages
    val message = msg.getBytes("UTF-8")
    val result: ArrayBuffer[String] = new ArrayBuffer[String]()

    while (message.length - currSplitPos > Constants.MAX_MESSAGE_LENGTH) {
      var pos = currSplitPos + Constants.MAX_MESSAGE_LENGTH

      // find the last whole unicode char
      while ((message(pos) & 0xc0) == 0x80) {
        pos -= 1
      }

      val str = new String(message.slice(currSplitPos, pos))
      result += str
      currSplitPos = pos
    }
    if (message.length - currSplitPos > 0) {
      result += new String(message.slice(currSplitPos, message.length))
    }

    result.toArray
  }
  def clearAllMessage()
  {
    var db = State.db
    if(db !=null)
      db.deleteAllMessage()
  }
  def sendUnsentMessages(contactKey: ContactKey, ctx: Context) {
    val db = State.db
    val unsentMessageList = db.getUnsentMessageList(contactKey)

    AntoxLog.debug(s"Sending ${unsentMessageList.length} unsent messages.", TAG)

    for (unsentMessage <- unsentMessageList) {
      contactKey match {
        case key: FriendKey =>
          EventBus.getDefault().post(new ToxSendInfoEvent(unsentMessage.message))
          sendMessage(ctx, key, unsentMessage.message,
            MessageType.toToxMessageType(unsentMessage.`type`), Some(unsentMessage.id))
        case key: GroupKey =>
          EventBus.getDefault().post(new ToxSendInfoEvent(unsentMessage.message))
          sendGroupMessage(ctx, key, unsentMessage.message,
            MessageType.toToxMessageType(unsentMessage.`type`), Some(unsentMessage.id))
      }
    }
  }
}
