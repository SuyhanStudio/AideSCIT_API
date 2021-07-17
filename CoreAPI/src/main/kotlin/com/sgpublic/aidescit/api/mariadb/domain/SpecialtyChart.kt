package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.mariadb.ids.SpecialtyChartIds
import org.json.JSONObject
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(SpecialtyChartIds::class)
@Table(name = "specialty_chart")
class SpecialtyChart {
    @Id
    @Column(name = "s_id")
    var specialty: Int = 0

    @Column(name = "s_name")
    var name: String = ""

    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }
}