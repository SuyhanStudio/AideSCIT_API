package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.UserInfoModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoController {
    @Autowired
    private lateinit var info: UserInfoModule
}