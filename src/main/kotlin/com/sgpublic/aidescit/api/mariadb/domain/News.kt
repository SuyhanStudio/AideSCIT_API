package com.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.json.JSONArray
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(News.Companion.NewsIdClass::class)
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
        class NewsIdClass: Serializable {
            var id: Int = 0

            var tid: Int = 0
        }

        class NewsList {
            var hasNext: Boolean = false
            val list: ArrayList<News> = arrayListOf()

            @JsonIgnore
            fun add(news: News){
                list.add(news)
            }
        }
    }
}