package com.stratagile.pnrouter.entity.events

import com.message.Message
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.JPullFileListRsp.ParamsBean.PayloadBean

class BeginDownloadForwad constructor(var msgId : String = "", var message : Message,var fileData : JPullFileListRsp.ParamsBean.PayloadBean)
