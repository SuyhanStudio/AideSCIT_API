package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.module.APIModule
import javax.persistence.*

/**
 * 数据表 user_info
 */
@Entity
@Table(name = "user_info")
class UserInfo {
    @Id
    @Column(name = "u_id")
    var username: String = ""

    @Column(name = "u_name")
    var name: String = ""

    @Column(name = "u_identify")
    var identify: Int = 0

    @Column(name = "u_level")
    var level: Int = 0

    @Column(name = "u_faculty")
    var faculty: Int = 0

    @Column(name = "u_specialty")
    var specialty: Int = 0

    @Column(name = "u_class")
    var classId: Short = 0

    @Column(name = "u_grade")
    var grade: Int = 0

    @Column(name = "u_info_expired")
    var expired: Long = APIModule.TS + 1296000

    @Transient
    fun isExpired() = expired < APIModule.TS
}