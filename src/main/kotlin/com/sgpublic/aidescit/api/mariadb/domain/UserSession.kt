package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.module.APIModule
import javax.persistence.*

/**
 * 数据表 user_token
 */
@Entity
@Table(name = "user_session")
class UserSession {
    @Id
    @Column(name = "u_id")
    var id: String = ""

    @Column(name = "u_password")
    var password: String = ""

    @Column(name = "u_session")
    var session: String = ""

    @Column(name = "u_session_expired")
    var expired: Long = APIModule.TS + 1800

    @Column(name = "u_token_effective")
    var effective: Short = 1

    @Transient
    var identify: Short = 0

    @Transient
    var verifyLocation: String = ""

    @Transient
    fun isExpired(): Boolean {
        return expired < APIModule.TS
    }

    @Transient
    fun isEffective(): Boolean {
        return effective.compareTo(1) == 0
    }
}