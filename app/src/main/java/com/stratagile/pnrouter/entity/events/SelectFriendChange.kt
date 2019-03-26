package com.stratagile.pnrouter.entity.events

import com.stratagile.pnrouter.db.UserEntity

class SelectFriendChange constructor(var friendNum : Int = 0,var groupNum:Int = 0, var isChec : Boolean, var user : UserEntity)