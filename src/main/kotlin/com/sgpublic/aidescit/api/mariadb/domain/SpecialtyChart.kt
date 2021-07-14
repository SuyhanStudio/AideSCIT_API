package com.sgpublic.aidescit.api.mariadb.domain

import org.json.JSONObject
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(SpecialtyChart.Companion.SpecialtyChartId::class)
@Table(name = "specialty_chart")
class SpecialtyChart {
    @Id
    @Column(name = "s_id")
    var specialty: Int = 0

    @Column(name = "s_name")
    var name: String = ""

    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }

    companion object {
        class SpecialtyChartId: Serializable {
            var faculty: Int = 0

            var specialty: Int = 0

            override fun equals(other: Any?): Boolean {
                if (super.equals(other)){
                    return true
                }
                if (other !is SpecialtyChartId){
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
    }
}