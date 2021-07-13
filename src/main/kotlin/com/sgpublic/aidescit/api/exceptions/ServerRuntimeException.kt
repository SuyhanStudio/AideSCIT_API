package com.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

class ServerRuntimeException(msg: String): ServletException(msg) {
    companion object {
        @JvmStatic
        val NETWORK_FAILED get() = ServerRuntimeException("网络请求失败")
        @JvmStatic
        val VIEWSTATE_NOT_FOUND get() = ServerRuntimeException("网络请求失败")
        @JvmStatic
        val INTERNAL_ERROR get() = ServerRuntimeException("服务器内部错误")
    }
}