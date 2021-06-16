package com.sgpublic.scit.tool.api.results

import org.json.JSONObject

/**
 * 处理成功结果
 */
open class ResultSuccess() : JSONObject() {
    init {
        put("code", 200)
        put("message", "success.")
    }
}