package io.github.sgpublic.aidescit.api.mariadb.ids

import java.io.Serializable

/**
 * 数据表 specialty_chart 多主键封装
 */
open class SpecialtyChartIds: Serializable {
    open var faculty: Int = 0

    open var specialty: Int = 0

    override fun equals(other: Any?): Boolean {
        if (other !is SpecialtyChartIds){
            return false
        }
        if (other.faculty != faculty){
            return false
        }
        if (other.specialty != specialty){
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = faculty
        result = 31 * result + specialty
        return result
    }
}