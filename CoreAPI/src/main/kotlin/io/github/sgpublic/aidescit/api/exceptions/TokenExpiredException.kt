package io.github.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 无效的 token，在用户提交 token 失效且所访问的服务必须 token 时抛出
 */
class TokenExpiredException: ServletException("无效的 token")