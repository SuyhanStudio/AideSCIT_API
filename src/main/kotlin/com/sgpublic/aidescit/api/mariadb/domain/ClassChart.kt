package com.sgpublic.aidescit.api.mariadb.domain

import org.json.JSONObject
import java.io.Serializable
import javax.persistence.*

/**
 * 数据表 class_chart
 */
@Entity
@IdClass(ClassChart.Companion.ClassChartId::class)
@Table(name = "class_chart")
class ClassChart {
    @Id
    @Column(name = "f_id")
    var faculty: Int = 0

    @Id
    @Column(name = "s_id")
    var specialty: Int = 0

    @Id
    @Column(name = "c_id")
    var classId: Short = 0

    @Id
    @Column(name = "grade")
    var grade: Short = 0

    @Column(name = "c_name")
    var name: String = ""

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }

    companion object {
        class ClassChartId: Serializable {
            var faculty: Int = 0

            var specialty: Int = 0

            var classId: Short = 0

            var grade: Short = 0

            override fun equals(other: Any?): Boolean {
                if (super.equals(other)){
                    return true
                }
                if (other !is ClassChartId){
                    return false
                }
                if (other.classId != classId){
                    return false
                }
                if (other.faculty != faculty){
                    return false
                }
                if (other.specialty != specialty){
                    return false
                }
                if (other.grade != grade){
                    return false
                }
                return true
            }

            override fun hashCode(): Int {
                var result = faculty
                result = 31 * result + specialty
                result = 31 * result + classId
                result = 31 * result + grade
                return result
            }
        }
    }
}