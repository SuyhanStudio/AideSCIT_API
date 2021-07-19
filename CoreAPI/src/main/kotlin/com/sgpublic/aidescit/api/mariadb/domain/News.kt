package com.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.sgpublic.aidescit.api.mariadb.ids.NewsIds
import org.json.JSONArray
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(NewsIds::class)
@Table(name = "news")
@Suppress("MemberVisibilityCanBePrivate")
class News {
    @Id
    @Column(name = "n_id")
    var nid: Int = 0

    @Id
    @Column(name = "n_type_id")
    var tid: Int = 0

    @Column(name = "n_title")
    var title: String = ""

    @Column(name = "n_summary")
    var summary: String = ""

    var images: ArrayList<String>
    get() = ArrayList<String>().apply {
        JSONArray(imagesContent).forEach {
            add(it as String)
        }
    }
    set(value) {
        imagesContent = JSONArray(value).toString()
    }

    @JsonIgnore
    @Column(name = "n_images")
    var imagesContent: String = ""

    @Column(name = "n_create_time")
    @JsonProperty("create_time")
    var createTime: String = ""

    companion object {
        class NewsList {
            @JsonProperty("has_next")
            var hasNext: Boolean = false
            val list: ArrayList<News> = arrayListOf()

            @JsonIgnore
            fun add(news: News){
                list.add(news)
            }
        }
    }
}