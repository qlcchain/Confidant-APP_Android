package com.stratagile.pnrouter.log

import com.alibaba.fastjson.JSON
import com.stratagile.pnrouter.log.BaseHandler
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by tony on 2017/11/27.
 */
class ObjectHandler(): BaseHandler() {

    override fun handle(obj: Any): Boolean {

        val s = L.getMethodNames()

        var msg = obj.javaClass.toString() + LoggerPrinter.BR + "║ "
        val objStr = JSON.toJSONString(obj)
        val jsonObject = JSONObject(objStr)
        var message = jsonObject.toString(LoggerPrinter.JSON_INDENT)
        message = message.replace("\n".toRegex(), "\n║ ")

        println(String.format(s, msg + message))

        return true
    }
}