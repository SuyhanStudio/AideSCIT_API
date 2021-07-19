package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.data.ExamSchedule
import org.jsoup.nodes.Document
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
        val doc1: Document = APIModule.executeDocument(
            url = url,
            cookies = APIModule.buildCookies(
                APIModule.COOKIE_KEY to session
            ),
            headers = APIModule.buildHeaders(
                "Referer" to url
            ),
            method = APIModule.METHOD_GET
        ).document
        val xnd: Boolean = checkSelected(doc1, "xnd", year)
        val xqd: Boolean = checkSelected(doc1, "xqd", semester)
        if (xnd && xqd){
            return parse(doc1.getElementById("DataGrid1"))
        }
        TODO("当默认未选中当前学期考试安排时逻辑待完善")
    }

    private fun checkSelected(doc: Document, id: String, value: Any): Boolean {
        doc.select("#$id").select("option").run {
            forEach {
                if (it.attr("value") != value.toString()){
                    return@forEach
                }
                if (it.hasAttr("selected")){
                    return true
                }
            }
            return false
        }
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