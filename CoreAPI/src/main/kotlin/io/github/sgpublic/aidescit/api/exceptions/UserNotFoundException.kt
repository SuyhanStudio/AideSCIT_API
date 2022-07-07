package io.github.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 用户不存在，在尝试获取不存在的用户的信息时抛出
 */
class UserNotFoundException: ServletException("用户不存在")