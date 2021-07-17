package com.sgpublic.aidescit.api.mariadb.domain

import org.json.JSONObject
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@Table(name = "faculty_chart")
class FacultyChart {
    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Column(name = "f_name")
    var name: String = ""

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }
}