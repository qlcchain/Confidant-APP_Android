package com.stratagile.pnrouter.utils

import java.math.BigDecimal

object NetUtils {
    fun parseSize(size : Long) : String{
        if (size < 1024 * 1024) {
            return "" + size / 1024 + " KB"
        }
        if (size <1024 * 1024 * 1024) {
            return "" + (size / 1024.0 / 1024.0).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
        }
        if (size < 1024 * 1024 * 1024 * 1024) {
            return "" + (size / 1024.0 / 1024.0 / 1024.0).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
        }
        return ""
    }
}