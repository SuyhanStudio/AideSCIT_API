package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.ClassSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/** 数据表 class_schedule 操作 */
@Repository
interface ClassScheduleRepository: JpaRepository<ClassSchedule, String> {
    /**
     * 调取课表信息
     * @param faculty 学院
     * @param specialty 专业
     * @param classId 班级编号
     * @param year 学年
     * @param semester 学期
     * @return 返回 [ClassSchedule]，若课表信息不存在则返回 null
     */
    @Query("select * from `class_schedule` where `t_faculty` = :tf and `t_specialty` = :ts and `t_class` = :tc and `t_school_year` = :y and `t_semester` = :s", nativeQuery = true)
    fun getSchedule(
        @Param("tf") faculty: Int, @Param("ts") specialty: Int,
        @Param("tc") classId: Short, @Param("y") year: String,
        @Param("s") semester: Short
    ): ClassSchedule?
}