package chat.tox.antox.viewholders

import java.io.File

import android.content._
import android.net.Uri
import android.view.View
import android.view.View.{OnClickListener, OnLongClickListener}
import android.widget._
import org.scaloid.common.LoggerTag
import rx.lang.scala.Subscription

class FileMessageHolder(val view: View) extends GenericMessageHolder(view) with OnClickListener with OnLongClickListener {

  private val TAG = LoggerTag(getClass.getSimpleName)



  private var file: File = _

  private var progressSub: Subscription = _

  private var imageLoadingSub: Option[Subscription] = None

  def render(): Unit = {

  }

  def setImage(file: File): Unit = {

  }

  def showFileButtons(): Unit = {

  }

  def showProgressBar(): Unit = {

  }

  def updateProgressBar(): Unit = {

  }

  def setProgressText(resID: Int): Unit = {

  }

  def setFileText(text: String): Unit = {

  }

 /* override def toggleReceived(): Unit = {
    // do nothing
  }*/

  override def onClick(view: View) {
    view match {
      case _: ImageView =>
        val i = new Intent()
        i.setAction(android.content.Intent.ACTION_VIEW)
        i.setDataAndType(Uri.fromFile(file), "image/*")
        //context.startActivity(i)

      case _ =>
    }
  }

  override def onLongClick(view: View): Boolean = {


    true
  }
}