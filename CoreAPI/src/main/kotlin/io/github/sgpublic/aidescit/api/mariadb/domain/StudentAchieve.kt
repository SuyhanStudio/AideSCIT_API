package io.github.sgpublic.aidescit.api.mariadb.domain

import com.google.gson.Gson
import io.github.sgpublic.aidescit.api.data.AchieveData
import io.github.sgpublic.aidescit.api.mariadb.ids.StudentAchieveIds
import io.github.sgpublic.aidescit.api.module.APIModule
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(StudentAchieveIds::class)
@Table(name = "student_achieve")
class StudentAchieve: Serializable {
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
        return Gson().fromJson(content, AchieveData::class.java)
    }

    @Transient
    fun setContent(data: AchieveData){
        content = Gson().toJson(data)
    }
}