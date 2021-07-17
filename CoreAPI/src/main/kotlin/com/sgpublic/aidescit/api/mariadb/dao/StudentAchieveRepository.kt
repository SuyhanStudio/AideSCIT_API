package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.StudentAchieve
import com.sgpublic.aidescit.api.mariadb.ids.StudentAchieveIds
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentAchieveRepository: JpaRepository<StudentAchieve, StudentAchieveIds> {
    @Query("select * from `student_achieve` where `u_id` = :u and `a_school_year` = :y and `a_semester` = :s",
        nativeQuery = true)
    fun getAchieve(@Param("u") username: String, @Param("y") year: String,
                   @Param("s") semester: Short): StudentAchieve?
}