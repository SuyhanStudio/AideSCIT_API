package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.data.AchieveData
import com.sgpublic.aidescit.api.mariadb.ids.StudentAchieveIds
import com.sgpublic.aidescit.api.module.APIModule
import org.json.JSONArray
import org.json.JSONObject
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(StudentAchieveIds::class)
@Table(name = "student_achieve")
class StudentAchieve {
    @Id
    @Column(name = "u_id")
    var username: String = ""

    @Id
    @Column(name = "a_school_year")
    var year: String = ""

    @Id
    @Column(name = "a_semester")
    var semester: Short = 0

    @Column(name = "a_content")
    private var content: String = ""

    @Column(name = "a_expired")
    private var expired: Long = APIModule.TS + 60

    @Transient
    fun isExpired(): Boolean {
        return expired < APIModule.TS
    }

    @Transient
    fun getContent(): AchieveData {
        val obj = JSONObject(content)
        val result = AchieveData()
        obj.getJSONArray("current").forEach {
            val current = it as JSONObject
            val item = AchieveData.Companion.CurrentAchieveItem()
            item.name = current.getString("name")
            item.paperScore = current.getDouble("paper_score")
            item.mark = current.getDouble("mark")
            item.retake = current.getDouble("retake")
            item.rebuild = current.getDouble("rebuild")
            item.credit = current.getDouble("credit")
            result.addCurrent(item)
        }
        obj.getJSONArray("failed").forEach {
            val failed = it as JSONObject
            val item = AchieveData.Companion.FailedAchieveItem()
            item.name = failed.getString("name")
            item.mark = failed.getDouble("mark")
            result.addFailed(item)
        }
        return result
    }

    @Transient
    fun setContent(data: AchieveData){
        content = JSONArray(data).toString()
    }
}