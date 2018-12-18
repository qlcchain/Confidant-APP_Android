package chat.tox.antox.callbacks

import android.content.Context
import chat.tox.antox.data.State
import chat.tox.antox.tox.ToxSingleton
import chat.tox.antox.utils.{AntoxLog, Base58}
import events.ToxReceiveFileNoticeEvent
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import org.greenrobot.eventbus.EventBus

class ToxCallbackListener(ctx: Context) extends ToxCoreEventListener[Unit] {
  val selfConnectionStatusCallback = new AntoxOnSelfConnectionStatusCallback(ctx)
  val messageCallback = new AntoxOnMessageCallback(ctx)
  val friendRequestCallback = new AntoxOnFriendRequestCallback(ctx)
  val connectionStatusCallback = new AntoxOnConnectionStatusCallback(ctx)
  val nameChangeCallback = new AntoxOnNameChangeCallback(ctx)
  val readReceiptCallback = new AntoxOnReadReceiptCallback(ctx)
  val statusMessageCallback = new AntoxOnStatusMessageCallback(ctx)
  val userStatusCallback = new AntoxOnUserStatusCallback(ctx)
  val typingChangeCallback = new AntoxOnTypingChangeCallback(ctx)
  val fileRecvCallback = new AntoxOnFileRecvCallback(ctx)
  val fileRecvChunkCallback = new AntoxOnFileRecvChunkCallback(ctx)
  val fileChunkRequestCallback = new AntoxOnFileChunkRequestCallback(ctx)
  val fileRecvControlCallback = new AntoxOnFileRecvControlCallback(ctx)
  val friendLosslessPacketCallback = new AntoxOnFriendLosslessPacketCallback(ctx)
  // --------- tox_file_seek callback is missing here !! ----------
  // tox_file_seek(Tox *tox, uint32_t friend_number, uint32_t file_number, uint64_t position, TOX_ERR_FILE_SEEK *error)
  // --------- tox_file_seek callback is missing here !! ----------

  override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    typingChangeCallback.friendTyping(friendInfo, isTyping)(Unit)
  }

  //接受文件片段
  override def fileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte])(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    fileRecvChunkCallback.fileRecvChunk(friendInfo, fileNumber, position, data)(Unit)
  }

  override def fileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    fileRecvControlCallback.fileRecvControl(friendInfo, fileNumber, control)(Unit)
  }

  override def friendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    connectionStatusCallback.friendConnectionStatus(friendInfo, connectionStatus)(Unit)
  }

  override def friendLosslessPacket(friendNumber: ToxFriendNumber, data: ToxLosslessPacket)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    friendLosslessPacketCallback.friendLosslessPacket(friendInfo, data)(Unit)
  }

  override def friendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    readReceiptCallback.friendReadReceipt(friendInfo, messageId)(Unit)
  }

  override def fileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    AntoxLog.debug("fileChunkRequest:"+friendInfo.key.toString)
    AntoxLog.debug("fileChunkRequest:"+fileNumber)
    AntoxLog.debug("fileChunkRequest:"+position)
    AntoxLog.debug("fileChunkRequest:"+length)
    fileChunkRequestCallback.fileChunkRequest(friendInfo, fileNumber, position, length)(Unit)
  }

  override def friendStatusMessage(friendNumber: ToxFriendNumber, message: ToxStatusMessage)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    statusMessageCallback.friendStatusMessage(friendInfo, message)(Unit)
  }

  override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    userStatusCallback.friendStatus(friendInfo, status)(Unit)
  }

  override def friendMessage(friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    messageCallback.friendMessage(friendInfo, messageType, timeDelta, message)(Unit)
  }

  //收到文件发送请求
  override def fileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    EventBus.getDefault().post(new ToxReceiveFileNoticeEvent(friendInfo.key.toString,fileNumber,filename.toString()))
    var filenameStr = filename.toString()
    var sourceName = Base58.decode(filenameStr.substring(filenameStr.indexOf(":")+1))
    fileRecvCallback.fileRecv(friendInfo, fileNumber, kind, fileSize, ToxFilename(sourceName))(Unit)
  }

  override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Unit): Unit = {
    selfConnectionStatusCallback.selfConnectionStatus(connectionStatus)(Unit)
  }

  override def friendName(friendNumber: ToxFriendNumber, name: ToxNickname)(state: Unit): Unit = {
    val friendInfo = State.db.getFriendInfo(ToxSingleton.tox.getFriendKey(friendNumber))
    nameChangeCallback.friendName(friendInfo, name)(Unit)
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: Unit): Unit = {
    friendRequestCallback.friendRequest(publicKey, timeDelta, message)(Unit)
  }

  /*
  val groupTopicChangeCallback = new AntoxOnGroupTopicChangeCallback(ctx)
  val groupPeerJoinCallback new AntoxOnPeerJoinCallback(ctx)
  val groupPeerExitCallback = new AntoxOnPeerExitCallback(ctx)
  val groupPeerlistUpdateCallback = new AntoxOnGroupPeerlistUpdateCallback(ctx)
  val groupNickChangeCallback = new AntoxOnGroupNickChangeCallback(ctx)
  val groupInviteCallback = new AntoxOnGroupInviteCallback(ctx)
  val groupSelfJoinCallback = new AntoxOnGroupSelfJoinCallback(ctx)
  val groupJoinRejectedCallback = new AntoxOnGroupJoinRejectedCallback(ctx)
  val groupSelfTimeoutCallback = new AntoxOnGroupSelfTimeoutCallback(ctx)
  val groupMessageCallback = new AntoxOnGroupMessageCallback(ctx) */
}