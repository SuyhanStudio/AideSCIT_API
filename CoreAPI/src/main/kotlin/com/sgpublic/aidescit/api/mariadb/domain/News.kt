package com.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sgpublic.aidescit.api.mariadb.ids.NewsIds
import org.json.JSONArray
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(NewsIds::class)
@Table(name = "news")
class News {
    @Id
    @Column(name = "n_id")
    var id: Int = 0

    @Id
    @Column(name = "n_type_id")
    var tid: Int = 0

    @Column(name = "n_title")
    var title: String = ""

    @Column(name = "n_summary")
    var summary: String = ""

    @JsonIgnore
    @Column(name = "n_images")
    private var images: String = ""

    @Column(name = "n_create_time")
    var createTime: String = ""

    @Transient
    fun getImages(): ArrayList<String> {
        return ArrayList<String>().apply {
            JSONArray(images).forEach {
                add(it as String)
            }
        }
    }

    @JsonIgnore
    @Transient
    fun setImages(list: ArrayList<String>){
        images = JSONArray(list).toString()
    }

    companion object {
        class NewsList {
            var hasNext: Boolean = false
            private val list: ArrayList<News> = arrayListOf()

            @JsonIgnore
            fun add(news: News){
                list.add(news)
            }
        }
    }
}