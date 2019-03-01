package com.stratagile.pnrouter.entity

class ShowAct : BaseBackA() {

    /**
     * data : {"isShow":1}
     */

    var data: DataBean? = null

    class DataBean {
        /**
         * isShow : 1
         */

        var isShow: Int = 0
    }
}
