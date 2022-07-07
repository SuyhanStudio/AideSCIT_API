package io.github.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import io.github.sgpublic.aidescit.api.core.util.toGson
import io.github.sgpublic.aidescit.api.mariadb.ids.NewsIds
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 news
 */
@Entity
@IdClass(NewsIds::class)
@Table(name = "news")
@Suppress("MemberVisibilityCanBePrivate")
class News: Serializable {
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

    var images: HashSet<String>
    get() = HashSet<String>().apply {
        for (image in Gson().fromJson(imagesContent, ArrayList::class.java)) {
            add(image as String)
        }
    }
    set(value) {
        imagesContent = value.toGson()
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