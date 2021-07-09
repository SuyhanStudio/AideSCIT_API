package com.sgpublic.aidescit.api.data

/**
 * token 对
 * @param access access_token
 * @param refresh refresh_token
 */
data class TokenPair(
    var access: String = "",
    var refresh: String = ""
)