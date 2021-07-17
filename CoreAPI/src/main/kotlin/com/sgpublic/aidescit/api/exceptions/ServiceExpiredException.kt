package com.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 服务请求过期，在用户传入 ts 参数无效或超出限定范围时抛出
 */
class ServiceExpiredException: ServletException("服务请求过期")