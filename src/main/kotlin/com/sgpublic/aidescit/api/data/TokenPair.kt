package com.sgpublic.aidescit.api.data

import org.json.JSONObject

/**
 * token
 * @param accessToken access_token
 * @param refreshToken refresh_token
 */
@Suppress("KDocUnresolvedReference")
class TokenPair {
    var accessToken: String = ""
    var refreshToken: String = ""

    override fun toString(): String {
        return JSONObject()
            .put("access_token", accessToken)
            .put("refresh_token", refreshToken)
            .toString()
    }
}