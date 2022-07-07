package io.github.sgpublic.aidescit.api.data

/**
 * 可选参数封装
 * @param faculty 学院代码
 * @param specialty 专业代码
 * @param classId 班级代码
 */
@Suppress("KDocUnresolvedReference")
open class ClassInfo {
    open var faculty: Int = -1

    open var specialty: Int = -1

    open var grade: Short = -1

    open var classId: Short = -1

    fun isNull(): Boolean {
        return faculty < 0 || specialty < 0 || grade < 0 || classId < 0
    }
}