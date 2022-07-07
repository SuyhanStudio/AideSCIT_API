package io.github.sgpublic.aidescit.api.controller

import io.github.sgpublic.aidescit.api.core.spring.BaseController
import io.github.sgpublic.aidescit.api.data.SemesterInfo
import io.github.sgpublic.aidescit.api.module.AchieveModule
import io.github.sgpublic.aidescit.api.module.UserInfoModule
import io.github.sgpublic.aidescit.api.result.FailedResult
import io.github.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AchieveController: BaseController() {
    @Autowired
    private lateinit var achieve: AchieveModule
    @Autowired
    private lateinit var info: UserInfoModule

    @RequestMapping("/aidescit/achieve")
    fun getAchieve(
        @RequestParam(name = "access_token") token: String,
        semester: SemesterInfo, sign: String
    ): Map<String, out Any?> {
        val check = checkAccessToken(token)
        if (info.get(check.getUsername()).isTeacher()){
            return FailedResult(-500, "什么？老师还有成绩单？(°Д°≡°Д°)")
        }
        achieve.get(check.getUsername(), semester.year, semester.semester).let {
            return SuccessResult(
                "achieve" to it
            )
        }
    }
}