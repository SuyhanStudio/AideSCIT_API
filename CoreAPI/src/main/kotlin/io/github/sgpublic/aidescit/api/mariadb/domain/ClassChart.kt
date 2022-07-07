package io.github.sgpublic.aidescit.api.mariadb.domain

import io.github.sgpublic.aidescit.api.mariadb.ids.ClassChartIds
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(ClassChartIds::class)
@Table(name = "class_chart")
class ClassChart: Serializable {
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
}