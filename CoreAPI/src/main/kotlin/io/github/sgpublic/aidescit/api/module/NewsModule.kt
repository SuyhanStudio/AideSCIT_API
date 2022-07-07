package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.core.util.Log
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.mariadb.dao.HeadlinesRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.NewsChartRepository
import io.github.sgpublic.aidescit.api.mariadb.dao.NewsRepository
import io.github.sgpublic.aidescit.api.mariadb.domain.Headlines
import io.github.sgpublic.aidescit.api.mariadb.domain.News
import io.github.sgpublic.aidescit.api.mariadb.domain.NewsChart
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern

/** 新闻模块 */
@Component
class NewsModule {
    @Autowired
    private lateinit var newsTypes: NewsChartRepository
    @Autowired
    private lateinit var news: NewsRepository
    @Autowired
    private lateinit var headlines: HeadlinesRepository

    /** 获取新闻类型字典 */
    fun getTypeChart(): ArrayList<NewsChart> {
        val types = newsTypes.getAll().run {
            return@run this.takeIf { isNotEmpty() }
                ?: refreshTypeChart()
        }
        return ArrayList<NewsChart>().apply {
            for (item in types){
                if (!item.isOut()) add(item)
            }
        }
    }

    /**
     * 刷新新闻类型字典
     * @see <a href="http://m.scit.cn/news.aspx">新闻类型字典获取地址</a>
     */
    private fun refreshTypeChart(): ArrayList<NewsChart> {
        val doc = APIModule.executeDocument(
            url = "http://m.scit.cn/news.aspx",
            method = APIModule.METHOD_GET
        )
        val types = ArrayList<NewsChart>()
        doc.select(".menu").select("ul").select("li").forEach {
            val item = NewsChart()
            it.select("a").run {
                item.tid = parseTid(attr("href")) ?: return@forEach
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
            if (!it.contains(NewsChart().apply { this.tid = tid })){
                throw ServerRuntimeException("新闻类别不存在")
            }
        }
        val url = "http://www.scit.cn/newslist${tid}_${page / 2 + 1}.htm"
        val doc = APIModule.executeDocument(
            url = url,
            method = APIModule.METHOD_GET
        )
        val indexStart = 10 * (page % 2)
        val result = News.Companion.NewsList()
        val pattern = Pattern.compile("_(\\d*)\\.")
        doc.select(".newslist").select("ul").select("li").forEachIndexed { index, element ->
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
                    if (length <= 2){
                        Log.d("id解析失败")
                        return@forEachIndexed
                    }
                    try {
                        result.add(getNewsById(
                            tid, substring(1, length - 1).toInt()
                        ))
                    } catch (_: ServerRuntimeException) { }
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
        val doc = APIModule.executeDocument(
            url = url,
            method = APIModule.METHOD_GET
        )
        val item = News()
        item.title = doc.select(".news_title").text().run {
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
        item.createTime = doc.select(".news_time").text().run {
            if (this == ""){
                throw ServerRuntimeException("新闻创建时间获取失败：$url")
            }
            val newsTime = split(" ")
            if (newsTime.isEmpty()){
                throw ServerRuntimeException("新闻创建时间解析失败：$url")
            }
            return@run newsTime[0]
        }
        val images = HashSet<String>()
        doc.select("p").forEach p@{
            if (images.size >= 3) return@p
            it.select("img").forEach img@{ src ->
                images.tryAdd(src.src, 3)
            }
            if (it.attr("align") == "center"
                || item.summary != "") return@p
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
                    return@p
                }
                if (summary.substring(summary.length - 1) == " "){
                    summary = summary.substring(0, summary.length - 1)
                }
                return@let summary
            }
        }
        doc.select("img").forEach {
            images.tryAdd(it.src, 3)
        }
        item.images = images
        item.nid = nid
        item.tid = tid
        news.save(item)
        return item
    }

    /** 获取新闻头条 */
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
            it.title = getNewsById(it.tid, it.nid).title
        }
        return headlines
    }

    /**
     * 刷新新闻头条
     * @see <a href="http://m.scit.cn/">四川工业科技学院官网移动版</a>
     */
    private fun refreshHeadlines(): List<Headlines> {
        val result = LinkedList<Headlines>()
        val doc = APIModule.executeDocument(
            url = "http://m.scit.cn/",
            method = APIModule.METHOD_GET
        )
        doc.select(".index_top_news_newslist").forEach {
            val item = Headlines()
            it.select("a").let { content ->
                if (!parseTidAndNid(item, content)) return@forEach
                item.image = content.select(".index_top_news_newslist_img")
                    .select("img").attr("src")
            }
            result.add(item)
        }
        doc.select(".index_gkyw_news_item_one").forEach {
            val item = Headlines()
            it.select("a").let { content ->
                if (!parseTidAndNid(item, content)) return@forEach
            }
            item.image = it.select(".index_gkyw_news_item_img")
                .select("a").select("img")
                .attr("src")
            result.add(item)
        }
        headlines.deleteAllInBatch()
        headlines.saveAllAndFlush(result)
        return result
    }

    private val nidPattern = Pattern.compile("[^t]id=(\\d+)")
    /**
     * 从网页元素中解析 tid 和 nid
     * @return 返回是否成功
     */
    private fun parseTidAndNid(item: Headlines, content: Elements): Boolean {
        val href = content.attr("href")
        item.tid = parseTid(href) ?: return false
        val matcher = nidPattern.matcher(href)
        if (!matcher.find()){
            Log.d("id获取失败")
            return false
        }
        matcher.group(0).run {
            if (length <= 4){
                Log.d("id解析失败")
                return false
            }
            item.nid = substring(4).toInt()
        }
        return true
    }

    private val tidPattern = Pattern.compile("tid=(\\d+)")
    /**
     * 从链接中解析新闻类型 ID
     * @return 返回 tid，若解析失败返回 null
     */
    private fun parseTid(url: String): Int? {
        val matcher = tidPattern.matcher(url)
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

/** 自动解析 img 标签中 src 参数，忽略 data:image */
val Element.src: String? get() {
    val src = attr("src")
    if (src == "") return null
    return src.takeIf {
        !src.startsWith("/")
    } ?: "http://www.scit.cn$src".takeIf {
        !src.startsWith("data:image")
    }
}

/**
 * 限制添加
 * @param item 要添加的 item，允许为空，若为空返回 false
 * @param maxSize 当前 HashSet 最大长度，若超出长度则返回 false
 * @return 是否添加成功
 */
fun <T> HashSet<T>.tryAdd(item: T?, maxSize: Int = -1): Boolean {
    return false.takeIf { item == null || size >= maxSize } ?: add(item!!)
}