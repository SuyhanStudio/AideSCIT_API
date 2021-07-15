package com.sgpublic.aidescit.api.mariadb.dao

import com.sgpublic.aidescit.api.mariadb.domain.Headlines
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HeadlinesRepository: JpaRepository<Headlines, Int> {
    /**
     * 获取所有头条新闻
     * @return 返回一个 [ArrayList]，若数据库中没有获取头条则返回空 list
     */
    fun getAll(): ArrayList<Headlines>
}