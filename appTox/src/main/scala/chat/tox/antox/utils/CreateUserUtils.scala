package chat.tox.antox.utils

import android.content.Context
import chat.tox.antox.data.State
import chat.tox.antox.data.UserDB
import chat.tox.antox.tox.ToxDataFile
import chat.tox.antox.toxme.{ToxData, ToxMeName}
import chat.tox.antox.wrapper.ToxAddress
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

object CreateUserUtils {
  def createToxData(accountName: String,ctx: Context): ToxData = {
    AntoxLog.debug("createToxData: " + accountName)
    val toxData = new ToxData
    val userDbLocal = State.userDb(ctx)
    if (userDbLocal.numUsers() == 0) {
      val toxOptions = new ToxOptions(Options.ipv6Enabled, Options.udpEnabled)
      val tox = new ToxCoreImpl(toxOptions)
      val toxDataFile = new ToxDataFile(ctx, accountName)
      toxDataFile.saveFile(tox.getSavedata)
      toxData.address = new ToxAddress(tox.getAddress.value)
      toxData.fileBytes = toxDataFile.loadFile()
      val userDb:UserDB = State.userDb(ctx)
      val toxMeName = ToxMeName.fromString(accountName, false)
      userDb.addUser(toxMeName, toxData.address, "")
    }else{
      State.login(accountName, ctx)
    }
    toxData
  }
}
