package com.stratagile.pnrouter.utils

import com.google.gson.Gson
import com.stratagile.pnrouter.entity.BaseData
import org.json.JSONObject


fun BaseData<*>.baseDataToJson() : String{
        //转化成json对象,这里不能用fastjson。会有大小写的问题
        return  Gson().toJson(this)
    }