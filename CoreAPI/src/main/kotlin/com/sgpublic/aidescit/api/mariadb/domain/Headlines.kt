package com.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sgpublic.aidescit.api.mariadb.ids.NewsIds
import com.sgpublic.aidescit.api.module.APIModule
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(NewsIds::class)
@Table(name = "news_headline")
class Headlines {
    @Id
    @Column(name = "h_id")
    var nid: Int = 0

    @Id
    @Column(name = "h_type_id")
    var tid: Int = 0

    @Column(name = "h_image")
    var image: String = ""

    @Transient
    var title: String = ""

    @JsonIgnore
    @Column(name = "h_expired")
    var expired: Long = APIModule.TS + 86400

    @JsonIgnore
    @Transient
    fun isExpired(): Boolean {
        return expired < APIModule.TS
    }
}