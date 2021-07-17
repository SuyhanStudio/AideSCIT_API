package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.News
import com.sgpublic.aidescit.api.mariadb.ids.NewsIds
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NewsRepository: JpaRepository<News, NewsIds> {
    /**
     * 获取指定的新闻信息
     * @param nid 该新闻在学校官网上的 ID
     * @param tid 该新闻在学校官网上所属类型的 ID
     * @return 返回 [News]，若指定新闻不在数据库中则返回 null
     */
    @Query("select * from `news` where `n_id` = :nid and `n_type_id` = :tid", nativeQuery = true)
    fun getNews(@Param("tid") tid: Int, @Param("nid") nid: Int): News?
}