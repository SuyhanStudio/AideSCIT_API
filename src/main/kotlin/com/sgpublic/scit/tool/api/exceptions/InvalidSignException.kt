package com.sgpublic.scit.tool.api.exceptions

import javax.servlet.ServletException

class InvalidSignException: ServletException("服务签名错误")