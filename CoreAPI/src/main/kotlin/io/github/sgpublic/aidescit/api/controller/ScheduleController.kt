package io.github.sgpublic.aidescit.api.controller

import io.github.sgpublic.aidescit.api.core.spring.BaseController
import io.github.sgpublic.aidescit.api.data.ClassInfo
import io.github.sgpublic.aidescit.api.data.SemesterInfo
import io.github.sgpublic.aidescit.api.module.ScheduleModule
import io.github.sgpublic.aidescit.api.result.SuccessResult
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
        sign: String, semester: SemesterInfo, clazz: ClassInfo
    ): Map<String, Any?> {
        val check = checkAccessToken(token)
        return SuccessResult("schedule" to schedule.get(
            check.getUsername(), semester.year, semester.semester, clazz
        ))
    }
}