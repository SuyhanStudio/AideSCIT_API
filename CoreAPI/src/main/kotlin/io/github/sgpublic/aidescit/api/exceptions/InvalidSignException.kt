package io.github.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 服务签名错误，用户提交 sign 参数校验失败时抛出。
 */
class InvalidSignException(local: String = "", submit: String = ""): ServletException("服务签名错误，提交：$submit，校验：$local")