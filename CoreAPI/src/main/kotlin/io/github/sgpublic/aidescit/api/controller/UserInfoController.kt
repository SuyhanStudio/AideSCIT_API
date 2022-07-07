package io.github.sgpublic.aidescit.api.controller

import io.github.sgpublic.aidescit.api.core.spring.BaseController
import io.github.sgpublic.aidescit.api.mariadb.dao.ClassChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.FacultyChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.SpecialtyChartRepository
import io.github.sgpublic.aidescit.api.module.UserInfoModule
import io.github.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoController: BaseController() {
    @Autowired
    private lateinit var info: UserInfoModule
    @Autowired
    private lateinit var classChart: ClassChartRepository
    @Autowired
    private lateinit var facultyChart: FacultyChartRepository
    @Autowired
    private lateinit var specialtyChart: SpecialtyChartRepository

    @RequestMapping("/aidescit/info")
    fun getUserInfo(@RequestParam(name = "access_token") token: String): Map<String, Any?> {
        val check = checkAccessToken(token)
        val user = info.get(check.getUsername())
        return SuccessResult(
            "info" to mapOf(
                "name" to user.name,
                "identify" to user.identify,
                "level" to user.level,
                "faculty" to facultyChart.getFacultyName(user.faculty),
                "specialty" to specialtyChart.getSpecialtyName(
                    user.faculty, user.specialty
                ),
                "class" to classChart.getClassName(
                    user.faculty, user.specialty, user.classId, user.grade
                ),
                "grade" to user.grade.toString()
            )
        )
    }
}