package io.github.sgpublic.aidescit.api.mariadb.domain

import io.github.sgpublic.aidescit.api.module.APIModule
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 user_token
 */
@Entity
@Table(name = "user_session")
class UserSession: Serializable {
    @Id
    @Column(name = "u_id")
    var id: String = ""

    @Column(name = "u_password")
    var password: String = ""

    @Column(name = "u_session")
    var session: String = ""

    @Column(name = "u_route")
    var route: String = ""

    @Column(name = "u_session_expired")
    var sessionExpired: Long = APIModule.TS + 1800

    @Column(name = "u_cookie")
    var cookie: String = ""

    @Column(name = "u_cookie_expired")
    var cookieExpired: Long = APIModule.TS + 3600

    @Column(name = "u_token_effective")
    var effective: Short = 1

    @Transient
    var identify: Short = 0

    @Transient
    var verifyLocation: String = ""

    @Transient
    fun isExpired(): Boolean {
        return sessionExpired < APIModule.TS
    }

    @Transient
    fun isEffective(): Boolean {
        return effective.compareTo(1) == 0
    }

    @Transient
    fun getCookie(): APIModule.Cookies {
        return APIModule.buildCookies(
            APIModule.Cookies.ROUTE to route,
            APIModule.Cookies.SESSION_ID to session
        )
    }
}