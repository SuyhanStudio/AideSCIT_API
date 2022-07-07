package io.github.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * refresh_token 无效或过期，用户刷新 token 但 refresh_token 无效或过期时抛出
 */
class InvalidRefreshTokenException: ServletException("refresh_token 无效或过期")