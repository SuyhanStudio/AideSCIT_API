package com.sgpublic.aidescit.api.data

import org.json.JSONObject
import org.springframework.web.bind.annotation.RequestParam

/**
 * token
 * @param access access_token
 * @param refresh refresh_token
 */
@Suppress("KDocUnresolvedReference")
class TokenPair (
    @RequestParam(name = "access_token", required = false, defaultValue = "")
    var access: String = "",
    @RequestParam(name = "refresh_token", required = false, defaultValue = "")
    var refresh: String = ""
) {
    override fun toString(): String {
        return JSONObject()
            .put("access_token", access)
            .put("refresh_token", refresh)
            .toString()
    }
}