package com.sgpublic.scit.tool.api.results.error

import com.sgpublic.scit.tool.api.results.BaseError

/**
 * 服务器内部错误
 */
class BadGatewayError: BaseError(-502, "服务器内部错误") {
    companion object {
        public fun getString(): String {
            return BadGatewayError().toString()
        }
    }
}