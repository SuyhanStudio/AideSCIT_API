package com.sgpublic.aidescit.api.mariadb.ids

import java.io.Serializable

/**
 * 数据表 news、news_headlines 多主键封装
 */
class NewsIds: Serializable {
    var id: Int = 0

    var tid: Int = 0

    override fun equals(other: Any?): Boolean {
        if (other !is NewsIds){
            return false
        }
        return other.id == id && other.tid ==  tid
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + tid
        return result
    }
}