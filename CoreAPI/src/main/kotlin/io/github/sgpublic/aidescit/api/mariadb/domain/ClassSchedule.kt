package io.github.sgpublic.aidescit.api.mariadb.domain

import com.google.gson.Gson
import io.github.sgpublic.aidescit.api.data.ScheduleData
import io.github.sgpublic.aidescit.api.module.APIModule
import javax.persistence.*

/**
 * 数据表 class_schedule
 */
@Entity
@Table(name = "class_schedule")
class ClassSchedule {
    @Id
    @Column(name = "t_id")
    var id: String = ""

    @Column(name = "t_faculty")
    var faculty: Int = 0

    @Column(name = "t_specialty")
    var specialty: Int = 0

    @Column(name = "t_class")
    var classId: Short = 0

    @Column(name = "t_grade")
    var grade: Short = 0

    @Column(name = "t_school_year")
    var year: String = ""

    @Column(name = "t_semester")
    var semester: Short = 0

    @Column(name = "t_content")
    var content: String = ""

    @Column(name = "t_expired")
    var expired: Long = APIModule.TS + 43200

    @Transient
    fun isExpired() = expired < APIModule.TS

    @Transient
    fun getContent(): ScheduleData {
        return Gson().fromJson(content, ScheduleData::class.java)
    }

    @Transient
    override fun toString(): String {
        return Gson().toJson(this)
    }
}