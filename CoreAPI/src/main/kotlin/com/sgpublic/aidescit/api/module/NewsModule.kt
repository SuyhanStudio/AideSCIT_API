package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.util.Log
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import com.sgpublic.aidescit.api.mariadb.dao.HeadlinesRepository
import com.sgpublic.aidescit.api.mariadb.dao.NewsChartRepository
import com.sgpublic.aidescit.api.mariadb.dao.NewsRepository
import com.sgpublic.aidescit.api.mariadb.domain.Headlines
import com.sgpublic.aidescit.api.mariadb.domain.News
import com.sgpublic.aidescit.api.mariadb.domain.NewsChart
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.regex.Pattern

/**
 * 新闻模块
 */
@Component
class NewsModule {
    @Autowired
    private lateinit var newsTypes: NewsChartRepository
    @Autowired
    private lateinit var news: NewsRepository
    @Autowired
    private lateinit var headlines: HeadlinesRepository

    /**
     * 获取新闻类型字典
     */
    fun getTypeChart(): ArrayList<NewsChart> {
        val types = newsTypes.getAll().run {
            return@run if (isEmpty()){
                refreshTypeChart()
            } else {
                this
            }
        }
        return ArrayList<NewsChart>().apply {
            for (item in types){
                if (!item.isOut()){
                    continue
                }
                add(item)
            }
        }
    }

    /**
     * 刷新新闻类型字典
     * @see <a href="http://m.scit.cn/news.aspx">新闻类型字典获取地址</a>
     */
    private fun refreshTypeChart(): ArrayList<NewsChart> {
        val doc1 = APIModule.executeDocument(
            url = "http://m.scit.cn/news.aspx",
            method = APIModule.METHOD_GET,
            checkViewstate = false
        ).document
        val types = ArrayList<NewsChart>()
        doc1.select(".menu").select("ul").select("li").forEach {
            val item = NewsChart()
            it.select("a").run {
                item.id = parseTid(this.attr("href"))
                    ?: return@forEach
                item.name = text()
            }
            types.add(item)
        }
        newsTypes.saveAll(types)
        return types
    }

    /**
     * 按新闻类型和页数列出新闻
     * @param tid 新闻类型 ID
     * @param page 页数
     * @see <a href="http://www.scit.cn/newslist9.htm">工科要闻（样例）</a>
     */
    fun listNewsByType(tid: Int, page: Int): News.Companion.NewsList {
        newsTypes.getAll().let {
            if (!it.contains(NewsChart().apply { this.id = tid })){
                throw ServerRuntimeException("新闻类别不存在")
            }
        }
        val url = "http://www.scit.cn/newslist${tid}_${page / 2 - 1}.htm"
        val doc1 = APIModule.executeDocument(
            url = url,
            method = APIModule.METHOD_GET,
            checkViewstate = false
        ).document
        val indexStart = 10 * (page % 2)
        val result = News.Companion.NewsList()
        val pattern = Pattern.compile("_(\\d*)\\.")
        doc1.select(".newslist").select("ul").select("li").forEachIndexed { index, element ->
            if (result.hasNext || index < indexStart){
                return@forEachIndexed
            }
            if (index == indexStart + 11){
                result.hasNext = true
                return@forEachIndexed
            }
            element.select("a").attr("href").let {
                if (it == "" || it.length < 2){
                    Log.w("tid获取失败，url: $url, index: $index")
                    return@forEachIndexed
                }
                val matcher = pattern.matcher(it)
                if (!matcher.find()){
                    Log.d("id获取失败，url: $url, index: $index")
                    return@forEachIndexed
                }
                matcher.group(0).run {
                    if (length <= 4){
                        Log.d("id解析失败")
                        return@forEachIndexed
                    }
                    result.add(getNewsById(
                        tid, substring(4).toInt()
                    ))
                }
            }
        }
        return result
    }

    /**
     * 指定新闻类型和 ID 获取新闻基本信息
     * @param tid 新闻类型 ID
     * @param nid 新闻 ID
     */
    private fun getNewsById(tid: Int, nid: Int): News {
        return news.getNews(tid, nid) ?: refreshNewsById(tid, nid)
    }

    /**
     * 指定新闻类型和 ID 刷新新闻基本信息
     * @param tid 新闻类型 ID
     * @param nid 新闻 ID
     * @see <a href="http://www.scit.cn/newsli9_28862.htm">新闻样例</a>
     */
    private fun refreshNewsById(tid: Int, nid: Int): News {
        val url = "http://www.scit.cn/newsli${tid}_${nid}.htm"
        val doc1 = APIModule.executeDocument(
            url = url,
            method = APIModule.METHOD_GET,
            checkViewstate = false
        ).document
        val item = News()
        item.title = doc1.select(".news_title").text().run {
            if (this == ""){
                throw ServerRuntimeException("新闻标题解析失败：$url")
            }
            var result = replace("\t", "")
                .replace("\n", "")
            result = Pattern.compile("(&nbsp;)+").matcher(result)
                .replaceAll(" ")
            if (result.substring(result.length - 1) == " "){
                result = result.substring(0, result.length - 1)
            }
            return@run result
        }
        item.createTime = doc1.select(".news_time").text().run {
            if (this == ""){
                throw ServerRuntimeException("新闻创建时间获取失败：$url")
            }
            val newsTime = split(" ")
            if (newsTime.isEmpty()){
                throw ServerRuntimeException("新闻创建时间解析失败：$url")
            }
            return@run newsTime[0]
        }
        val images = ArrayList<String>()
        doc1.select("p").forEach {
            if (images.size >= 3){
                return@forEach
            }
            it.select("img").attr("src").let { src ->
                if (src == ""){
                    return@let
                }
                images.add(if (src.startsWith("/")) {
                    "http://www.scit.cn$src"
                } else {
                    src
                })
            }
            if (item.summary != ""){
                return@forEach
            }
            if (it.attr("align") == "center"){
                return@forEach
            }
            item.summary = it.text().let { text ->
                var summary = text
                if (summary.length > 80){
                    summary = summary.substring(0, 80)
                }
                summary = summary.replace("\t", "")
                    .replace("\n", "")
                summary = Pattern.compile("(&nbsp;)+").matcher(summary)
                    .replaceAll(" ")
                if (summary.isEmpty()){
                    return@forEach
                }
                if (summary.substring(summary.length - 1) == " "){
                    summary = summary.substring(0, summary.length - 1)
                }
                return@let summary
            }
        }
        doc1.select("img").forEach {
            if (images.size >= 3){
                return@forEach
            }
            it.attr("src").let { src ->
                if (src == ""){
                    return@forEach
                }
                val imageUrl = if (src.startsWith("/")) {
                    "http://www.scit.cn$src"
                } else {
                    src
                }
                if (images.contains(imageUrl)){
                    return@forEach
                }
                images.add(imageUrl)
            }
        }
        item.setImages(images)
        item.id = nid
        item.tid = tid
        news.save(item)
        return item
    }

    /**
     * 获取新闻头条
     */
    fun getHeadlines(): ArrayList<Headlines> {
        val headlines = headlines.getAll()
        for (item in headlines){
            if (!item.isExpired()){
                continue
            }
            headlines.clear()
            break
        }
        if (headlines.isEmpty()){
            headlines.addAll(refreshHeadlines())
        }
        headlines.forEach {
            it.title = getNewsById(it.id, it.tid).title
        }
        return headlines
    }

    /**
     * 刷新新闻头条
     * @see <a href="http://m.scit.cn/">四川工业科技学院官网移动版</a>
     */
    private fun refreshHeadlines(): ArrayList<Headlines> {
        val result = ArrayList<Headlines>()
        val doc1 = APIModule.executeDocument(
            url = "http://m.scit.cn/",
            method = APIModule.METHOD_GET,
            checkViewstate = false
        ).document
        doc1.select(".index_top_news_newslist").forEach {
            val item = Headlines()
            it.select("a").let { content ->
                if (!parseTidAndNid(item, content)){
                    return@forEach
                }
                item.image = content.select(".index_top_news_newslist_img")
                    .select("img").attr("src")
            }
            result.add(item)
        }
        doc1.select(".index_gkyw_news_item_one").forEach {
            val item = Headlines()
            it.select("a").let { content ->
                if (!parseTidAndNid(item, content)){
                    return@forEach
                }
            }
            item.image = it.select(".index_gkyw_news_item_img")
                .select("a").select("img")
                .attr("src")
            result.add(item)
        }
        headlines.saveAll(result)
        return result
    }

    /**
     * 从网页元素中解析 tid 和 nid
     * @return 返回是否成功
     */
    private fun parseTidAndNid(item: Headlines, content: Elements): Boolean {
        val href = content.attr("href")
        item.id = parseTid(href)
            ?: return false
        val matcher = Pattern.compile(
            "[^t]id=(\\d+)"
        ).matcher(href)
        if (!matcher.find()){
            Log.d("id获取失败")
            return false
        }
        matcher.group(0).run {
            if (length <= 4){
                Log.d("id解析失败")
                return false
            }
            item.tid = substring(4).toInt()
        }
        return true
    }

    /**
     * 从链接中解析新闻类型 ID
     * @return 返回 tid，若解析失败返回 null
     */
    private fun parseTid(url: String): Int? {
        val matcher = Pattern.compile(
            "tid=(\\d+)"
        ).matcher(url)
        if (!matcher.find()){
            Log.w("tid获取失败，url: $url")
            return null
        }
        matcher.group(0).run {
            if (length <= 4){
                Log.w("tid解析失败，url: $url")
                return null
            }
            return substring(4).toInt()
        }
    }
}