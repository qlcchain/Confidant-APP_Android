package com.stratagile.pnrouter.ui.activity.user

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ScanViewModel : ViewModel() {
    var toAddUserId = MutableLiveData<String>()

    var freindChange = MutableLiveData<Long>()
}