package com.stratagile.pnrouter.method

import android.text.Spannable
import android.widget.EditText

class AtTools {
    private val methods = arrayOf(Weibo,WeChat, QQ)//arrayOf(Weibo,WeChat, QQ)
    private var iterator: Iterator<Method> = methods.iterator()
    private val methodContext = MethodContext()
    fun newSpannable(user: User,toAdressEdit:EditText,type:Int): Spannable {

        val method = circularMethod(type)
        methodContext.method = method
        methodContext.init(toAdressEdit)
        return methodContext.newSpannable(user)
    }

    private tailrec fun circularMethod(type:Int): Method {
        return methods.get(type)
    }
}