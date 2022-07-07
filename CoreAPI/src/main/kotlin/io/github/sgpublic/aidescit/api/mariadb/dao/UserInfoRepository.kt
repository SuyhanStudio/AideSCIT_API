package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/** 数据表 user_info 操作 */
@Repository
interface UserInfoRepository: JpaRepository<UserInfo, String> {
    /**
     * 调取用户信息
     * @param username 用户学号/工号
     * @return 返回 [UserInfo]，若用户信息不存在则返回 null
     */
    @Query("select * from `user_info` where `u_id` = :uid", nativeQuery = true)
    fun getByUsername(@Param("uid") username: String): UserInfo?
}