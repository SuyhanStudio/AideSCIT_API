package io.github.sgpublic.aidescit.api.mariadb.ids

/**
 * 数据表 class_chart 多主键封装
 */
class ClassChartIds: SpecialtyChartIds() {
    override var faculty: Int = 0

    override var specialty: Int = 0

    var classId: Short = 0

    var grade: Short = 0

    override fun equals(other: Any?): Boolean {
        if (other !is ClassChartIds){
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