package com.stratagile.pnrouter.utils

object NetUtils {
    fun parseSize(size : Int) : String{
        if (size < 1024) {
            return "" + size + "KB"
        }
        if (size <1024 * 1024) {
            return "" + (size / 1024) + "MB"
        }
        if (size < 1024 * 1024 * 1024) {
            return "" + (size / 1024 / 1024) + "GB"
        }
        return ""
    }
}