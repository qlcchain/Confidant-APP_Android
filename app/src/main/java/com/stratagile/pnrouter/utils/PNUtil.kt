package com.stratagile.pnrouter.utils

import com.google.gson.GsonBuilder


fun Any.baseDataToJson() : String{
        //转化成json对象,这里不能用fastjson。会有大小写的问题
    val gson = GsonBuilder().disableHtmlEscaping().create()
    return  gson.toJson(this)
    }