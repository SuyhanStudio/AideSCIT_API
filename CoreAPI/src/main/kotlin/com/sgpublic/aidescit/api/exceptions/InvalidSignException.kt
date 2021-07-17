package com.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 服务签名错误，用户提交 sign 参数校验失败时抛出。
 */
class InvalidSignException: ServletException("服务签名错误")