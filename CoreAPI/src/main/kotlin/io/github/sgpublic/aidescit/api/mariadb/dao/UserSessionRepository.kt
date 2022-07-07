package io.github.sgpublic.aidescit.api.mariadb.dao

import io.github.sgpublic.aidescit.api.mariadb.domain.UserSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 数据表 user_token 操作
 */
@Repository
interface UserSessionRepository: JpaRepository<UserSession, String> {
    /**
     * 获取用户密码
     * @param id 用户学号/工号
     * @return 返回用户加盐密文密码
     */
    @Query("select `u_password` from `user_session` where `u_id` = :id", nativeQuery = true)
    fun getUserPassword(@Param("id") id: String): String?

    /**
     * 从数据库获取用户 ASP.NET_SessionId
     * @param id 用户学号/工号
     * @return 返回 [UserSession]
     */
    @Query("select * from `user_session` where `u_id` = :id", nativeQuery = true)
    fun getUserSession(@Param("id") id: String): UserSession?
}