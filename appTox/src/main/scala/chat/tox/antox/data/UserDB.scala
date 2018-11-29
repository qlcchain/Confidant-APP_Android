
package chat.tox.antox.data

import android.content.Context
import android.database.{Cursor, Observable}
import chat.tox.antox.toxme.ToxMeName
import chat.tox.antox.wrapper.{CallReply, ToxAddress, UserInfo}

import scala.collection.mutable.ArrayBuffer

object UserDB {

 /* private val TAG = LoggerTag(getClass.getSimpleName)

  val databaseName = "userdb"
  val sqlBrite = SqlBrite.create()

  class DatabaseHelper(context: Context) extends SQLiteOpenHelper(context, databaseName, null, USER_DATABASE_VERSION) {
    private val CREATE_TABLE_USERS: String =
      s"""CREATE TABLE IF NOT EXISTS $TABLE_USERS ( _id integer primary key ,
          |$COLUMN_NAME_PROFILE_NAME text,
          |$COLUMN_NAME_PASSWORD text,
          |$COLUMN_NAME_NICKNAME text,
          |$COLUMN_NAME_STATUS text,
          |$COLUMN_NAME_STATUS_MESSAGE text,
          |$COLUMN_NAME_AVATAR text,
          |$COLUMN_NAME_LOGGING_ENABLED boolean,
          |$COLUMN_NAME_TOXME_DOMAIN text);""".stripMargin

    private val CREATE_TABLE_CALL_REPLIES: String =
      s"""CREATE TABLE IF NOT EXISTS $TABLE_CALL_REPLIES ( _id integer primary key ,
          |$COLUMN_NAME_PROFILE_NAME text,
          |$COLUMN_NAME_CALL_REPLY text,
          |FOREIGN KEY($COLUMN_NAME_PROFILE_NAME) REFERENCES $TABLE_USERS($COLUMN_NAME_PROFILE_NAME))""".stripMargin

    override def onCreate(db: SQLiteDatabase) {
      db.execSQL(CREATE_TABLE_USERS)
      db.execSQL(CREATE_TABLE_CALL_REPLIES)
    }

    override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {
      AntoxLog.info(s"Upgrading UserDB from version $oldVersion to $newVersion", TAG)

      for (currVersion <- oldVersion to newVersion) {
        currVersion match {
          case 1 =>
            if (!DatabaseUtil.isColumnInTable(db, TABLE_USERS, COLUMN_NAME_AVATAR)) {
              db.execSQL(s"ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_NAME_AVATAR text")
            }
          case 2 =>
            db.execSQL(s"ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_NAME_LOGGING_ENABLED integer")
            db.execSQL(s"UPDATE $TABLE_USERS SET $COLUMN_NAME_LOGGING_ENABLED = $TRUE")
          case 4 =>
            db.execSQL(s"ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_NAME_TOXME_DOMAIN text")
            db.execSQL(s"UPDATE $TABLE_USERS SET $COLUMN_NAME_TOXME_DOMAIN = 'toxme.io' ")
          case 5 =>
            db.execSQL(CREATE_TABLE_CALL_REPLIES)
          case _ =>
        }
      }
    }

  }*/

}

class UserDB(ctx: Context) {

 case class NotLoggedInException(message: String = "Invalid request. No active user found.") extends RuntimeException



  def activeUser: Option[String] = {
    val user = null
    user
  }

  def getActiveUser: String = activeUser.getOrElse(throw new NotLoggedInException())



  def close() {

  }

  def login(username: String): Unit = {

  }

  def addUser(toxMeName: ToxMeName, toxId: ToxAddress, password: String) {

  }

  def addDefaultCallReplies(profileName: String): Unit = {

  }

  def doesUserExist(username: String): Boolean = {
    val exists =false

    exists
  }

  def deleteActiveUser(): Unit = {

  }

  private def userDetailsQuery(username: String): String =""


  private def userInfoFromCursor(cursor: Cursor): Option[UserInfo] = {
    val userInfo: Option[UserInfo] = null


    userInfo
  }

  def getActiveUserDetails: UserInfo =
    getUserDetails(getActiveUser).get //fail fast

  def getUserDetails(username: String): Option[UserInfo] = {
    val query = userDetailsQuery(username)

    val userInfo = null

    userInfo
  }

  def activeUserDetailsObservable(): Observable[UserInfo] =
    userDetailsObservable(getActiveUser)

  def userDetailsObservable(username: String): Observable[UserInfo] = {
    val userInfo = null

    userInfo
  }

  def updateActiveUserDetail(detail: String, newDetail: String): Unit = {

  }

  def updateActiveUserDetail(detail: String, newDetail: Boolean): Unit = {

  }

  private def updateUserDetail(username: String, detail: String, newDetail: String) {

  }

  def updateUserDetail(username: String, detail: String, newDetail: Boolean) {

  }

  def getActiveUserCallRepliesObservable: Observable[ArrayBuffer[CallReply]] = {
      val callReplies = null


      callReplies

  }
  def loggedIn: Boolean = activeUser.isDefined
  def logout(): Unit = {

  }
  def getActiveUserCallReplies: ArrayBuffer[CallReply] = {
    val userInfo = null

    userInfo
  }

  def updateActiveUserCallReply(callReply: CallReply): Unit = {

  }

  def numUsers(): Int = {

      val count = 0

      count
  }

  def getAllProfiles: ArrayBuffer[String] = {
    val profiles = new ArrayBuffer[String]()
    profiles
  }
}
