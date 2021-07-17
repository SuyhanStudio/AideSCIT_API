package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.NewsChart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NewsChartRepository: JpaRepository<NewsChart, Int> {
    /**
     * 获取所有新闻类型字典
     * @return 返回一个 [ArrayList]，若字典不存在则返回空 list
     */
    @Query("select * from `news_chart`", nativeQuery = true)
    fun getAll(): ArrayList<NewsChart>
}