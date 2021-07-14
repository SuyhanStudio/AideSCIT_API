package com.sgpublic.aidescit.api.core.param

import org.json.JSONObject

/**
 * 可选参数封装
 * @param accessToken access_token，默认为空字符串
 */
@Suppress("KDocUnresolvedReference")
class Token {
    var accessToken: String = ""

    override fun toString(): String {
        return JSONObject()
            .put("access_token", accessToken)
            .toString()
    }
}