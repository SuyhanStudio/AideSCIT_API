package io.github.sgpublic.aidescit.api.exceptions

/**
 * 服务请求过期，在用户传入 ts 参数无效或超出限定范围时抛出
 */
class ServiceExpiredException: Exception("服务请求过期")