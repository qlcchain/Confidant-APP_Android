package chat.tox.antox.data

import android.content.Context

sealed abstract class CallEventKind(val kindId: Int, val imageRes: Int, private val messageRes: Int) {
  def message(context: Context): String = context.getResources.getString(messageRes)
}

object CallEventKind {

 case object Invalid extends CallEventKind(-1, 0, 1)

  case object Incoming extends CallEventKind(0, 0, 1)

  case object Outgoing extends CallEventKind(1, 0, 1)

  case object Rejected extends CallEventKind(2, 0, 1)

  case object Unanswered extends CallEventKind(3, 0, 1)

  case object Missed extends CallEventKind(4, 0, 1)

  case object Answered extends CallEventKind(5, 0, 1)

  case object Ended extends CallEventKind(6, 0, 1)

  case object Cancelled extends CallEventKind(7, 0, 1)

  val values: Set[CallEventKind] =
    Set(
      Invalid,
      Incoming,
      Outgoing,
      Rejected,
      Unanswered,
      Missed,
      Answered,
      Ended,
      Cancelled
    )
}
