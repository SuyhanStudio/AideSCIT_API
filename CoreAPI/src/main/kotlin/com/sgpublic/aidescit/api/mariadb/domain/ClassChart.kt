package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.mariadb.ids.ClassChartIds
import org.json.JSONObject
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(ClassChartIds::class)
@Table(name = "class_chart")
class ClassChart {
    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Id
    @Column(name = "s_id")
    var specialty: Int = 0

    @Id
    @Column(name = "c_id")
    var classId: Short = 0

    @Id
    @Column(name = "grade")
    var grade: Short = 0

    @Column(name = "c_name")
    var name: String = ""

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }
}