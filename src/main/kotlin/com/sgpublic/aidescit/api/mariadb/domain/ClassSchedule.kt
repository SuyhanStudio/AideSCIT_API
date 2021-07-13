package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.module.APIModule
import org.json.JSONObject
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
    var grade: Int = 0

    @Column(name = "t_school_year")
    var year: String = ""

    @Column(name = "t_semester")
    var semester: Short = 0

    @Column(name = "t_content")
    var content: String = ""

    @Column(name = "t_expired")
    var expired: Long = APIModule.TS + 1296000

    @Transient
    fun isExpired() = expired < APIModule.TS

    @Transient
    fun getContent() = JSONObject(content)
}