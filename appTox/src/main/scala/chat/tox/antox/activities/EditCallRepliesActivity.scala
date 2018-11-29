package chat.tox.antox.activities

import android.support.v7.app.AppCompatActivity

class EditCallRepliesActivity extends AppCompatActivity {

  /*var callRepliesSubscription: Option[Subscription] = None

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_call_replies)

    val thisActivity = this

    getSupportActionBar.setTitle(R.string.title_activity_edit_call_replies)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    val callRepliesListView = findViewById(R.id.call_replies_list).asInstanceOf[ListView]
    val callRepliesAdapter = new EditCallRepliesAdapter(this, ArrayBuffer.empty)
    callRepliesSubscription =
      Some(State.userDb(this)
        .getActiveUserCallRepliesObservable
        .observeOn(AndroidMainThreadScheduler())
        .subscribe(callReplies => {
          callRepliesAdapter.setNotifyOnChange(false)

          callRepliesAdapter.clear()
          callRepliesAdapter.addAll(callReplies)

          callRepliesAdapter.notifyDataSetChanged()
        }))

    callRepliesListView.setAdapter(callRepliesAdapter)

    callRepliesListView.setOnItemClickListener(new OnItemClickListener {
      override def onItemClick(adapter: AdapterView[_], view: View, position: Int, id: Long): Unit = {
        val callReply = adapter.getItemAtPosition(position).asInstanceOf[CallReply]

        val callReplyDialogTag = "call_reply_dialog"
        val editCallReplyDialog = EditCallReplyDialog.newInstance(callReply)
        editCallReplyDialog.show(thisActivity.getSupportFragmentManager, callReplyDialogTag)
      }
    })
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        onBackPressed()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

  class EditCallRepliesAdapter(context: Context, callReplies: ArrayBuffer[CallReply])
    extends ArrayAdapter(context, R.layout.item_call_reply, R.id.call_reply, callReplies) {

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      super.getView(position, convertView, parent)

      val callReply: CallReply = getItem(position)

      val view =
        if (convertView == null) {
          LayoutInflater.from(getContext).inflate(R.layout.item_call_reply, parent, false)
        } else convertView

      val callReplyTextView = view.findViewById(R.id.call_reply).asInstanceOf[TextView]
      callReplyTextView.setText(callReply.reply)

      view
    }

    override def getViewTypeCount: Int = 1
  }*/

}
