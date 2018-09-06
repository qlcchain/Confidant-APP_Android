package com.stratagile.pnrouter.data.web

import javax.inject.Inject

class StaticCredentialsProvider @Inject constructor(override val user: String, override val password: String, override val signalingKey: String) : CredentialsProvider
