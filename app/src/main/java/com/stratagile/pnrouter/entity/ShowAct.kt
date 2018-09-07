package com.stratagile.pnrouter.entity

class ShowAct : BaseBack() {

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
