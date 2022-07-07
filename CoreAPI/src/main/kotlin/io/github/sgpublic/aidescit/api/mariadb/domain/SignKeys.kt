package io.github.sgpublic.aidescit.api.mariadb.domain

import com.google.gson.Gson
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 sign_keys
 */
@Entity
@Table(name = "sign_keys")
class SignKeys: Serializable {
    @Id
    @Column(name = "app_key")
    var appKey: String = ""

    @Column(name = "app_secret")
    var appSecret: String = ""

    @Column(name = "platform")
    var platform: String = ""

    @Column(name = "mail")
    var mail: String = ""

    @Column(name = "build")
    var build: Long = 0

    @Column(name = "available")
    var available: Short = 1
}