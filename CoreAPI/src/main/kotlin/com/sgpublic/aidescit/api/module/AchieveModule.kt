package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.data.AchieveData
import com.sgpublic.aidescit.api.mariadb.dao.StudentAchieveRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AchieveModule {
    @Autowired
    private lateinit var achieve: StudentAchieveRepository
    @Autowired
    private lateinit var session: SessionModule

    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
                   semester: Short = SemesterInfoProperty.SEMESTER): AchieveData {
        achieve.getAchieve(username, year, semester).run {
            if (this == null || isExpired()){
                return refresh(username, year, semester)
            }
            return getContent()
        }
    }

    private fun refresh(username: String, year: String, semester: Short): AchieveData {
        val session = session.get(username).session
        val url = "http://218.6.163.93:8081/xscj.aspx?xh=$username"
        var doc = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            method = APIModule.METHOD_GET
        )

        val button1 = when {
            year == "all" -> {
                "在校学习成绩查询"
            }
            semester.compareTo(0) == 0 -> {
                "按学年查询"
            }
            else -> {
                "按学期查询"
            }
        }
        doc = doc.post(
            "ddlXN" to year,
            "ddlXQ" to semester,
            "txtQSCJ" to 0,
            "txtZZCJ" to 100,
            "Button1" to button1
        )
        val result = AchieveData()
        doc.getElementById("DataGrid1").select("tr").forEachIndexed { index, tr ->
            if (index == 0){
                return@forEachIndexed
            }
            val item = AchieveData.Companion.CurrentAchieveItem()
            tr.select("td").run {
                item.name = get(1).text()
                item.paperScore = get(3).text().toDoubleOrNull()
                item.mark = get(4).text().toDouble()
                item.retake = get(6).text().toDoubleOrNull()
                item.rebuild = get(7).text().toDoubleOrNull()
                item.credit = get(8).text().toDouble()
            }
            result.addCurrent(item)
        }
        doc.getElementById("Datagrid3").select("tr").forEachIndexed { index, tr ->
            if (index == 0){
                return@forEachIndexed
            }
            val item = AchieveData.Companion.FailedAchieveItem()
            tr.select("td").run {
                item.name = get(1).text()
                item.mark = get(3).text().toDouble()
            }
            result.addFailed(item)
        }
        return result
    }
}