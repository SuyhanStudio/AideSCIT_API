package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.ClassChart
import io.github.sgpublic.aidescit.api.mariadb.ids.ClassChartIds
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


/** 数据表 class_chart 操作 */
@Repository
interface ClassChartRepository: JpaRepository<ClassChart, ClassChartIds> {
    /**
     * 调取班级名称字典
     * @param faculty 班级所属学院
     * @param specialty 班级所属专业
     * @param classId 班级编号
     * @param grade 年级
     * @return 返回班级名称，若字典不存在则返回 null
     */
    @Query(
        "select `c_name` from `class_chart` where `f_id` = :f " +
                "and `s_id` = :s and `c_id` = :c and `grade` = :g",
        nativeQuery = true
    )
    fun getClassName(
        @Param("f") faculty: Int, @Param("s") specialty: Int,
        @Param("c") classId: Short, @Param("g") grade: Short
    ): String?
}