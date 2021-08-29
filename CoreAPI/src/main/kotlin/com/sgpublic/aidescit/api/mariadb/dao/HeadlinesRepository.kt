package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.Headlines
import com.sgpublic.aidescit.api.mariadb.ids.NewsIds
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface HeadlinesRepository: JpaRepository<Headlines, NewsIds> {
    /**
     * 获取所有头条新闻
     * @return 返回一个 [ArrayList]，若数据库中没有获取头条则返回空 list
     */
    @Query("select * from `news_headline`", nativeQuery = true)
    fun getAll(): ArrayList<Headlines>

//    @Suppress("SqlWithoutWhere")
//    @Query("delete from `news_headline`", nativeQuery = true)
//    fun removeAll()
}