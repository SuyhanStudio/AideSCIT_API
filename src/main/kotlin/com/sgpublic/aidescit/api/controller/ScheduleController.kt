package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.controller.param.SemesterInfo
import com.sgpublic.aidescit.api.core.util.TokenUtil
import com.sgpublic.aidescit.api.data.TokenPair
import com.sgpublic.aidescit.api.exceptions.TokenExpiredException
import com.sgpublic.aidescit.api.module.ScheduleModule
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController {
    @Autowired
    private lateinit var schedule: ScheduleModule

    @RequestMapping("/aidescit/schedule")
    fun getSchedule(param: SemesterInfo, @RequestParam(name = "access_token") token: String,
                    sign: String): Map<String, Any>{
        val check = TokenUtil.startVerify(TokenPair(token))
        if (check.isAccessTokenExpired()){
            throw TokenExpiredException()
        }
        val data = schedule.get(check.getUsername(), param.year, param.semester)
        return SuccessResult(
            "schedule" to data
        )
    }
}