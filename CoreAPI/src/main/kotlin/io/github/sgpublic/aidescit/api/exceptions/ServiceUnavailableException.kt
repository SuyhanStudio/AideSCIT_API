package io.github.sgpublic.aidescit.api.exceptions

/**
 * 服务不可用，在用户访问还未完成的服务时抛出
 */
class ServiceUnavailableException: Exception("服务不可用")