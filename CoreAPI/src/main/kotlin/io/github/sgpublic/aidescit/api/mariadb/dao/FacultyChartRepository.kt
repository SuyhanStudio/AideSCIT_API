package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.FacultyChart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FacultyChartRepository: JpaRepository<FacultyChart, Int> {
    /**
     * 调取学院名称字典
     * @param faculty 学院 ID
     * @return 返回学院名称，若字典不存在则返回 null
     */
    @Query("select `f_name` from `faculty_chart` where `f_id` = :f", nativeQuery = true)
    fun getFacultyName(@Param("f") faculty: Int): String?
}