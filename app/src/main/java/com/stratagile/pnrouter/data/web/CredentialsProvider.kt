package com.stratagile.pnrouter.data.web

interface CredentialsProvider {
    val user: String
    val password: String
    val signalingKey: String
}