package com.stratagile.pnrouter.method

import android.text.Spannable
import android.widget.EditText

interface Method {

    fun init(editText: EditText)
    fun newSpannable(user: User): Spannable
}