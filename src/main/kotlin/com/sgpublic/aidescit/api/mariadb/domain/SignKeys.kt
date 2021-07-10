package com.sgpublic.aidescit.api.mariadb.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 数据表 sign_keys
 */
@Entity
@Table(name = "sign_keys")
class SignKeys {
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