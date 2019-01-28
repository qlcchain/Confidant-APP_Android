package com.stratagile.pnrouter.entity.file

import scala.collection.generic.BitOperations

class FileOpreateType(var icon :String, var name : String) {

}

class UpLoadFile(var name : String, var isDownLoad : Boolean = true, var isComplete : Boolean = false, var isStop : Boolean = false, var progress : Int = 0, var total : Int = 0, var speed : Int = 0)