package com.sgpublic.scit.tool.api.mariadb.dao

import com.sgpublic.scit.tool.api.mariadb.domain.Hitokoto
import com.sgpublic.scit.tool.api.module.APIModule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/** 数据表 hitokoto 操作 */
@Repository
interface HitokotoRepository: JpaRepository<Hitokoto, Long> {
    /**
     * hitokoto 间隔时间设置（单位：秒），默认值为 86400
     */
    private val timeExpire: Int get() = 86400

    /**
     * 检查距离上次获取 hitokoto 是否超过所设置的间隔时间 [timeExpire]
     * @param time 间隔时间，使用此方法时请将此参数留空
     * @return 返回查询结果 [List]，若此 List 长度不为 0 则未超过
     */
    @Query("select `h_id` from `hitokoto` where `h_insert_at`>:time", nativeQuery = true)
    fun tryGet(@Param("time") time: Long = APIModule.ts - timeExpire): List<Hitokoto>

    /**
     * 从数据库随机调取已保存的 hitokoto
     * @return 返回查询结果 [List]，正常情况下此 List 长度应为 1
     */
    @Query("select `h_content`,`h_from`,`h_length` from `hitokoto` where `h_id` >= (select FLOOR(RAND() * (select MAX(`h_id`) from `hitokoto`))) order by `h_id` limit 1", nativeQuery = true)
    fun randGet(): List<Hitokoto>
}