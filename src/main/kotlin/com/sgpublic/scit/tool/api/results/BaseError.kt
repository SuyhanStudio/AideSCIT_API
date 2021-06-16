package com.sgpublic.scit.tool.api.results

import com.sgpublic.scit.tool.api.core.ResponseJSONObject
import org.json.JSONObject

open class BaseError(code: Int, message: String) : ResponseJSONObject() {
    init {
        put("code", code)
        put("message", message)
    }
}