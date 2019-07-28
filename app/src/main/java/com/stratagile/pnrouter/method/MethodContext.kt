package com.stratagile.pnrouter.method

import android.text.Spannable
import android.widget.EditText

class MethodContext: Method {
    var method: Method? = null
    override fun init(editText: EditText) {
        method?.init(editText)
    }

    override fun newSpannable(user: User): Spannable {
        return method?.newSpannable(user) ?: throw NullPointerException("method: null")
    }

}