package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.core.spring.BaseController
import com.sgpublic.aidescit.api.data.SemesterInfo
import com.sgpublic.aidescit.api.module.ScheduleModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController: BaseController()  {
    @Autowired
    private lateinit var schedule: ScheduleModule

    @RequestMapping("/aidescit/schedule")
    fun getSchedule(
        @RequestParam(name = "access_token") token: String,
        sign: String, param: SemesterInfo
    ): Map<String, Any> {
        val check = checkAccessToken(token)
        return schedule.get(check.getUsername(), param.year, param.semester)
    }
}