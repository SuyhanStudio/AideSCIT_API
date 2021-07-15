package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.NewsModule
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NewsController {
    @Autowired
    private lateinit var news: NewsModule

    @RequestMapping("/aidescit/news/type")
    fun getType(): Map<String, Any> {
        return SuccessResult(
            "types" to news.getTypeChart()
        )
    }

    @RequestMapping("/aidescit/news/headlines")
    fun getHeadlines(): Map<String, Any> {
        return SuccessResult(
            "headlines" to news.getHeadlines()
        )
    }

    @RequestMapping("/aidescit/news/headlines")
    fun getNews(tid: Int, page: Int, sign: String): Map<String, Any> {
        return SuccessResult(
            "news" to news.listNewsByType(tid, page)
        )
    }
}