package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.SessionModule
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {
    @Autowired
    lateinit var session: SessionModule

    @RequestMapping("/aidescit/login")
    fun login(username: String, password: String, sign: String): Map<String, Any>{
        session.get(username, password)
        return SuccessResult()
    }
}