package io.github.sgpublic.aidescit.api.mariadb.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 数据表 class_chart
 */
@Entity
@Table(name = "faculty_chart")
class FacultyChart: Serializable {
    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Column(name = "f_name")
    var name: String = ""
}