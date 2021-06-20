package com.sgpublic.scit.tool.api.exceptions

import javax.servlet.ServletException

/** 服务签名错误 */
class InvalidSignException: ServletException("服务签名错误")