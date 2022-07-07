package io.github.sgpublic.aidescit.api.exceptions

/**
 * 运行时异常，一般与上游服务器有关（即教务系统）
 */
class ServerRuntimeException(msg: String): Exception(msg) {
    companion object {
        @JvmStatic
        val NETWORK_FAILED get() = ServerRuntimeException("网络请求失败")
        @JvmStatic
        val VIEWSTATE_NOT_FOUND get() = ServerRuntimeException("__VIEWSTATE 获取失败")
        @JvmStatic
        val VIEWSTATE_GENERATOR_NOT_FOUND get() = ServerRuntimeException("__VIEWSTATEGENERATOR 获取失败")
        @JvmStatic
        val INTERNAL_ERROR get() = ServerRuntimeException("服务器内部错误")
    }
}