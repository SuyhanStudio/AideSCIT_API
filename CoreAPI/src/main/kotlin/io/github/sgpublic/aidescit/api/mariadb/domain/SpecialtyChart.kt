package io.github.sgpublic.aidescit.api.mariadb.domain

import io.github.sgpublic.aidescit.api.mariadb.ids.SpecialtyChartIds
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(SpecialtyChartIds::class)
@Table(name = "specialty_chart")
class SpecialtyChart: Serializable {
    @Id
    @Column(name = "s_id")
    var specialty: Int = 0

    @Column(name = "s_name")
    var name: String = ""

    @Id
    @Column(name = "f_id")
    var faculty: Int = 0
}