package com.sgpublic.aidescit.api.exceptions

import javax.servlet.ServletException

/**
 * 服务不可用，在用户访问还未完成的服务时抛出
 */
class ServiceUnavailableException: ServletException("服务不可用")