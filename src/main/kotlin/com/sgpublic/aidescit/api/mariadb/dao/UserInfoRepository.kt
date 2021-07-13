package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/** 数据表 user_info 操作 */
@Repository
interface UserInfoRepository: JpaRepository<UserInfo, String> {
    @Query("select * from `user_info` where `u_id` = :uid", nativeQuery = true)
    fun getByUsername(@Param("uid") username: String): UserInfo?
}