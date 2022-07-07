package io.github.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 密码错误，用户登录密码错误时抛出
 */
class WrongPasswordException(username: String): ServletException("<$username> 账号或密码错误")