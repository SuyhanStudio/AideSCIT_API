package io.github.sgpublic.aidescit.api.mariadb.domain

import io.github.sgpublic.aidescit.api.data.ClassInfo
import io.github.sgpublic.aidescit.api.module.APIModule
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 user_info
 */
@Entity
@Table(name = "user_info")
class UserInfo: ClassInfo(), Serializable {
    @Id
    @Column(name = "u_id")
    var username: String = ""

    @Column(name = "u_name")
    var name: String = ""

    @Column(name = "u_identify")
    var identify: Short = 0

    @Column(name = "u_level")
    var level: Short = 0

    @Column(name = "u_faculty")
    override var faculty: Int = 0

    @Column(name = "u_specialty")
    override var specialty: Int = 0

    @Column(name = "u_class")
    override var classId: Short = 0

    @Column(name = "u_grade")
    override var grade: Short = 0

    @Column(name = "u_info_expired")
    var expired: Long = APIModule.TS + 1296000

    @Transient
    fun isExpired() = expired < APIModule.TS

    @Transient
    fun isTeacher(): Boolean {
        return identify.compareTo(0) != 0
    }

    @Transient
    fun isStudent(): Boolean {
        return identify.compareTo(0) == 0
    }
}