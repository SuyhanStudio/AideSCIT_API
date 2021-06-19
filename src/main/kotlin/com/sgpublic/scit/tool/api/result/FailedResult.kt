package com.sgpublic.scit.tool.api.result

import com.sgpublic.scit.tool.api.core.SimpleMap

class FailedResult(code: Int, message: String) : SimpleMap() {
    companion object {
        @JvmStatic
        val INVALID_SIGN = FailedResult(-400, "服务签名错误")
        @JvmStatic
        val INTERNAL_SERVER_ERROR = FailedResult(-500, "服务器内部错误")
    }

    init {
        put("code", code)
        put("message", message)
    }
}