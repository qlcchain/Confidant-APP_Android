package com.stratagile.pnrouter.utils

import android.content.Context
import android.content.SharedPreferences
import com.pawegio.kandroid.e
import java.util.*

object SpUtil {
    private var sp: SharedPreferences? = null

    fun putBoolean(context: Context, key: String, value: Boolean?) {
        if(context ==null  ||  key == null)
        {
            return
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putBoolean(key, value!!).commit()
    }

    fun getBoolean(context: Context, key: String, defValue: Boolean?): Boolean {
        if(context ==null  ||  key == null)
        {
            return false
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getBoolean(key, defValue!!)
    }

    fun putString(context: Context, key: String, value: String) {
        if(context ==null  ||  key == null)
        {
            return
        }
        if(key.indexOf("message_") > -1 && key.equals(""))
        {
            var aa =""
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putString(key, value).commit()
    }

    fun getString(context: Context, key: String, defValue: String): String? {
        if(context ==null  ||  key == null)
        {
            return ""
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getString(key, defValue)
    }

    fun putInt(context: Context, key: String, value: Int) {
        if(context ==null  ||  key == null)
        {
            return
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putInt(key, value).commit()
    }

    fun getInt(context: Context, key: String, defValue: Int): Int {
        if(context ==null  ||  key == null)
        {
            return 0
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getInt(key, defValue)
    }

    fun putLong(context: Context, key: String, value: Long) {
        if(context ==null  ||  key == null)
        {
            return
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putLong(key, value).commit()
    }

    fun getLong(context: Context, key: String, defValue: Long): Long {
        if(context ==null  ||  key == null)
        {
            return 1
        }
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        return sp!!.getLong(key, defValue)
    }
    fun getAll(context: Context): Map<String, Object> {
        if (sp == null && context !=null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        return sp!!.all as Map<String, Object>
    }
}
