package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.Hitokoto
import com.sgpublic.aidescit.api.module.APIModule
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
     * @param time 间隔时间
     * @return 返回未过期的 hitokoto 的 id，若不存在则返回 null
     */
    @Query("select `h_id` from `hitokoto` where `h_insert_at`>:time", nativeQuery = true)
    fun tryGet(@Param("time") time: Long = APIModule.TS - timeExpire): Long?

    /**
     * 从数据库随机调取已保存的 hitokoto
     * @return 返回 [Hitokoto]
     */
    @Query("select * from `hitokoto` where `h_id` >= (" +
            "select FLOOR(RAND() * (select MAX(`h_id`) from `hitokoto`))" +
            ") order by `h_id` limit 1", nativeQuery = true)
    fun randGet(): Hitokoto
}