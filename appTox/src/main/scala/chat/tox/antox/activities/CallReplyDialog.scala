package chat.tox.antox.activities

import android.support.v4.app.DialogFragment

trait CallReplySelectedListener {


  //def onCallReplySelected(maybeReply: Option[String]): Unit
}

class CallReplyDialog extends DialogFragment {
 /* override def onAttach(context: Context): Unit = {
    super.onAttach(context)
    context match {
      case activity: Activity =>
        if (!activity.isInstanceOf[CallReplySelectedListener]) {
          throw new ClassCastException(s"${activity.toString} must implement ${classOf[CallReplyDialog]}")
        }

      case _ =>
    }

  }

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    super.onCreateDialog(savedInstanceState)

    val replies = State.userDb(getActivity).getActiveUserCallReplies
    val customReply = getActivity.getResources.getString(R.string.call_incoming_reply_dialog_custom)
    val replyStrings = (replies.map(_.reply) :+ customReply).toArray

    val builder = new AlertDialog.Builder(getActivity)
      .setTitle(R.string.call_incoming_reply_dialog_title)
      .setItems(replyStrings.map(reply => reply: CharSequence), new OnClickListener {
        override def onClick(dialog: DialogInterface, which: Int): Unit = {
          val selectedReply: Option[String] =
            if (which == replyStrings.indices.last) {
              None
            } else {
              Some(replyStrings(which))
            }

          getActivity.asInstanceOf[CallReplySelectedListener].onCallReplySelected(selectedReply)
        }
      })

    builder.create()
  }*/
}
