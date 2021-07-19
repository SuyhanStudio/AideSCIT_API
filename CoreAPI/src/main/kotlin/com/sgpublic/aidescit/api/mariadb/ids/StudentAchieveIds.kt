package com.sgpublic.aidescit.api.mariadb.ids

import java.io.Serializable

/**
 * 数据表 student_achieve 多主键封装
 */
class StudentAchieveIds: Serializable {
    var username: String = ""

    var year: String = ""

    var semester: Short = 0

    override fun equals(other: Any?): Boolean {
        if (other !is StudentAchieveIds){
            return false
        }
        if (other.username != username){
            return false
        }
        if (other.year != year){
            return false
        }
        if (other.semester != semester){
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + semester
        return result
    }
}