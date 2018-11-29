package chat.tox.antox.activities

import chat.tox.antox.wrapper._

class GroupChatActivity extends GenericChatActivity[GroupKey] {

/*  var photoPath: String = null

  override def getKey(key: String): GroupKey = new GroupKey(key)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    findViewById(R.id.info).setVisibility(View.GONE)
    findViewById(R.id.call).setVisibility(View.GONE)
    findViewById(R.id.video).setVisibility(View.GONE)

    statusIconView.setVisibility(View.GONE)
  }

  override def onResume(): Unit = {
    super.onResume()
    val thisActivity = this
    val db = State.db
    titleSub = db.groupInfoList
      .subscribeOn(IOScheduler())
      .observeOn(AndroidMainThreadScheduler())
      .subscribe(groupInfo => {
        val id = activeKey
        val mGroup: Option[GroupInfo] = groupInfo.find(groupInfo => groupInfo.key == id)
        thisActivity.setDisplayName(mGroup.map(_.getDisplayName).getOrElse(""))
      })
  }

  def onClickVoiceCallFriend(v: View): Unit = {}

  def onClickVideoCallFriend(v: View): Unit = {}

  def onClickInfo(v: View): Unit = {
    val profile = new Intent(this, classOf[GroupProfileActivity])
    profile.putExtra("key", activeKey.toString)
    startActivity(profile)
  }

  override def onPause(): Unit = {
    super.onPause()
  }

  override def sendMessage(message: String, messageType: ToxMessageType, context: Context): Unit = {
    MessageHelper.sendGroupMessage(context, activeKey, message, messageType, None)
  }

  override def setTyping(typing: Boolean): Unit = {
    // not yet implemented in toxcore
  }

  override def onClickInfo(): Unit = {
    //TODO add a group profile activity
  }

  override def onClickVideoCall(clickLocation: Location): Unit = {
    // not yet implemented in toxav
  }

  override def onClickVoiceCall(clickLocation: Location): Unit = {
    // not yet implemented in toxav
  }*/
}

