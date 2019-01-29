package com.stratagile.pnrouter.entity.file

import scala.collection.generic.BitOperations

class FileOpreateType(var icon :String, var name : String) {

}

class UpLoadFile(var path : String,var fileSize:Long, var isDownLoad : Boolean = true, var isComplete : Boolean = false, var isStop : Boolean = false, var segSeqResult : Int = 0, var segSeqTotal : Int = 0, var speed : Int = 0,var SendGgain:Boolean = false)