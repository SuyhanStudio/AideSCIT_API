package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.core.spring.BaseController
import com.sgpublic.aidescit.api.data.SemesterInfo
import com.sgpublic.aidescit.api.module.ExamScheduleModule
import com.sgpublic.aidescit.api.result.FailedResult
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ExamController: BaseController() {
    @Autowired
    private lateinit var exam: ExamScheduleModule

    @RequestMapping("/aidescit/exam")
    fun getExam(
        @RequestParam(name = "access_token") token: String,
        semester: SemesterInfo, sign: String
    ): Map<String, Any> {
        val check = checkAccessToken(token)
        val result = exam.get(check.getUsername(), semester.year, semester.semester)
        if (result.isEmpty()){
            return FailedResult.EMPTY_RESULT
        }
        return SuccessResult(
            "exam" to result
        )
    }
}