package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.SpecialtyChart
import io.github.sgpublic.aidescit.api.mariadb.ids.SpecialtyChartIds
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SpecialtyChartRepository: JpaRepository<SpecialtyChart, SpecialtyChartIds> {
    /**
     * 调取专业名称字典
     * @param faculty 专业所属学院
     * @param specialty 专业
     * @return 返回专业名称，若课表信息不存在则返回 null
     */
    @Query("select `s_name` from `specialty_chart` where `f_id` = :f and `s_id` = :s", nativeQuery = true)
    fun getSpecialtyName(@Param("f") faculty: Int, @Param("s") specialty: Int): String?

    /**
     * 列出所有专业名称字典
     * @return 返回专业名称，若课表信息不存在则返回 null
     */
    @Query("select * from `specialty_chart`", nativeQuery = true)
    fun getAll(): List<SpecialtyChart>
}