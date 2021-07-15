package com.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sgpublic.aidescit.api.module.APIModule
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(News.Companion.NewsIdClass::class)
@Table(name = "news_headline")
class Headlines {
    @Id
    @Column(name = "h_id")
    var id: Int = 0

    @Id
    @Column(name = "h_type_id")
    var tid: Int = 0

    @Column(name = "h_image")
    var image: String = ""

    @JsonIgnore
    @Column(name = "h_expired")
    var expired: Long = APIModule.TS + 86400

    @JsonIgnore
    @Transient
    fun isExpired(): Boolean {
        return expired < APIModule.TS
    }


    companion object {
        class NewsIdClass: Serializable {
            var id: Int = 0

            var tid: Int = 0
        }
    }
}