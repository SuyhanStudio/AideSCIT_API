package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.data.ExamSchedule
import com.sgpublic.aidescit.api.data.ViewStateDocument
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExamScheduleModule {
    @Autowired
    private lateinit var session: SessionModule

    fun get(username: String, year: String = SemesterInfoProperty.YEAR,
            semester: Short = SemesterInfoProperty.SEMESTER): ArrayList<ExamSchedule> {
        val url = "http://218.6.163.93:8081/xskscx.aspx?xh=$username"
        val session: String = session.get(username).session
        val doc: ViewStateDocument = APIModule.executeDocument(
            url = url,
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            cookies = APIModule.buildCookies(
                APIModule.Cookies.SESSION_ID to session
            ),
            method = APIModule.METHOD_GET
        )
        val xnd: Boolean = doc.checkSelectedOption("#xnd", year)
        val xqd: Boolean = doc.checkSelectedOption("#xqd", semester.toString())
        if (xnd && xqd){
            return parse(doc.getElementById("DataGrid1")!!)
        }
        TODO("当默认未选中当前学期考试安排时逻辑待完善")
    }

    private fun parse(doc: Element): ArrayList<ExamSchedule> {
        val result = ArrayList<ExamSchedule>()
        doc.select("tr").forEachIndexed { index, tr ->
            if (index == 0){
                return@forEachIndexed
            }
            val item = ExamSchedule()
            tr.select("td").run {
                item.name = get(1).text()
                item.time = get(3).text()
                item.location = get(4).text()
                item.setNum = get(6).text().toShort()
            }
            result.add(item)
        }
        return result
    }
}