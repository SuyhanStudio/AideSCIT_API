package com.sgpublic.aidescit.api.manager

import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.core.util.RSAUtil
import com.sgpublic.aidescit.api.exceptions.InvalidPasswordFormatException

class SessionManager(private val username: String, private var password: String = "") {

    fun getUserPassword(): String {
        if (password == ""){
            TODO("从数据库调取用户密码")
        }
        return RSAUtil.decode(password).apply {
            if (length <= 8){
                Log.d("用户密码解析错误")
                throw InvalidPasswordFormatException()
            }
        }.substring(8)
    }
}