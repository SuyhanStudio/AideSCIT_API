package io.github.sgpublic.aidescit.api.mariadb.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * 数据表 news_chart
 */
@Entity
@Table(name = "news_chart")
class NewsChart {
    @Id
    @Column(name = "n_type_id")
    var tid: Int = 0

    @Column(name = "n_name")
    var name: String = ""

    @JsonIgnore
    @Column(name = "n_out")
    private var out: Short = 0

    @JsonIgnore
    @Transient
    fun isOut(): Boolean {
        return out.compareTo(1) == 0
    }

    @JsonIgnore
    @Transient
    override fun equals(other: Any?): Boolean {
        if (other !is NewsChart){
            return false
        }
        return other.tid == tid
    }

    @JsonIgnore
    @Transient
    override fun hashCode(): Int {
        return tid
    }
}