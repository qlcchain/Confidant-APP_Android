package com.stratagile.pnrouter.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object SpUtil {
    private var sp: SharedPreferences? = null

    fun putBoolean(context: Context, checkOnOff: String, value: Boolean?) {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putBoolean(checkOnOff, value!!).commit()
    }

    fun getBoolean(context: Context, key: String, defValue: Boolean?): Boolean {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getBoolean(key, defValue!!)
    }

    fun putString(context: Context, key: String, value: String) {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putString(key, value).commit()
    }

    fun getString(context: Context, key: String, defValue: String): String? {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getString(key, defValue)
    }

    fun putInt(context: Context, key: String, value: Int) {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putInt(key, value).commit()
    }

    fun getInt(context: Context, key: String, defValue: Int): Int {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)

        }
        return sp!!.getInt(key, defValue)
    }

    fun putLong(context: Context, key: String, value: Long) {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        sp!!.edit().putLong(key, value).commit()
    }

    fun getLong(context: Context, key: String, defValue: Long): Long {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        return sp!!.getLong(key, defValue)
    }
    fun getAll(context: Context): Map<String, Object> {
        if (sp == null || context ==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        }
        return sp!!.all as Map<String, Object>
    }
}
