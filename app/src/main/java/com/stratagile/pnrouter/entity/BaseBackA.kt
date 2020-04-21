package com.stratagile.pnrouter.entity

/**
 * Created by huzhipeng on 2018/1/9.
 */

open class BaseBackA {

    /**
     * code : 0
     * msg : success
     */

    var code: String? = null
    var msg: String? = null
    override fun toString(): String {
        return "BaseBackA(code=$code, msg=$msg)"
    }
}
