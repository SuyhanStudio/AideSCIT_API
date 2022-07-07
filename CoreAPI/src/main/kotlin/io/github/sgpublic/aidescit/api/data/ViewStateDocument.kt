package io.github.sgpublic.aidescit.api.data

import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.module.APIModule
import okhttp3.FormBody
import okhttp3.Headers
import okio.IOException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Document 的二次封装，支持快捷创建附带 __VIEWSTATE 的 POST 请求
 * @param url 创建此 ViewStateDocument 时的网络请求所使用的 URL
 * @param document [org.jsoup.nodes.Document]
 * @param cookies 创建此 ViewStateDocument 时的网络请求所使用的 Cookie
 * @param headers 创建此 ViewStateDocument 时的网络请求所使用的 Header
 */
class ViewStateDocument(
    private val url: String,
    private val document: Document,
    private val cookies: APIModule.Cookies? = null,
    private val headers: Headers? = null,
) {
    /**
     * 复用当前请求的 cookies 和 headers 创建新的 GET 请求，同时修改 headers 中的 Referer
     */
    @Throws(IOException::class, IllegalStateException::class, ServerRuntimeException::class)
    fun get(url: String, body: FormBody? = null): ViewStateDocument {
        return APIModule.executeDocument(url, body, headers?.run {
            Headers.Builder().apply {
                for (index in 0 until size){
                    val name = name(index)
                    if (name == APIModule.REFERER) {
                        add(APIModule.REFERER, url)
                    } else {
                        add(name, value(index))
                    }
                }
            }.build()
        }, cookies, APIModule.METHOD_GET)
    }

    /**
     * 复用当前请求的 cookies、headers 和 url 创建新的 POST 请求，附带当前请求的 __VIEWSTATE 等信息
     */
    @Throws(IOException::class, IllegalStateException::class, ServerRuntimeException::class)
    fun post(vararg pairs: Pair<String, Any>): ViewStateDocument {
        val body = mutableMapOf(*pairs)
        return APIModule.executeDocument(url, FormBody.Builder().apply {
            getEventArgument()?.let {
                add("__EVENTARGUMENT", it)
            }
            getLastFocus()?.let {
                add("__LASTFOCUS", it)
            }
            add("__VIEWSTATE", getViewState())
            add("__VIEWSTATEGENERATOR", getGenerator())
            body.forEach { (key, value) ->
                add(key, value.toString())
            }
        }.build(), headers, cookies, APIModule.METHOD_POST)
    }

    /**
     * 获取当前请求的 #__EVENTARGUMENT
     */
    private fun getEventArgument(): String? {
        return getAttrValue("#__EVENTARGUMENT")
    }

    /**
     * 获取当前请求的 #__LASTFOCUS
     */
    private fun getLastFocus(): String? {
        return getAttrValue("#__LASTFOCUS")
    }

    /**
     * 获取当前请求的 #__VIEWSTATE
     */
    private fun getViewState(): String {
        getAttrValue("#__VIEWSTATE").let {
            return it ?: throw ServerRuntimeException.VIEWSTATE_NOT_FOUND
        }
    }

    /**
     * 获取当前请求的 #__VIEWSTATEGENERATOR
     */
    private fun getGenerator(): String {
        getAttrValue("#__VIEWSTATEGENERATOR").let {
            return it ?: throw ServerRuntimeException.VIEWSTATE_GENERATOR_NOT_FOUND
        }
    }

    /**
     * 获取当前请求结果的 Document 中指定 ID 的 Element 中的 value 参数值
     */
    private fun getAttrValue(cssQuery: String): String? {
        val elements = document.select(cssQuery)
        return if (elements.size != 0){
            elements.attr("value")
        } else { null }
    }

    /**
     * 使用 css 选择器选择 Elements
     * @see Document.select
     */
    fun select(cssQuery: String): Elements = document.select(cssQuery)

    /**
     * 使用 css 选择器选择 Element
     * @see Document.getElementById
     */
    fun getElementById(id: String): Element? = document.getElementById(id)

    /**
     * 检查当前请求结果的 Document 中指定 ID 的 select 组件所选中的 value 值是否为指定的值
     */
    fun checkSelectedOption(cssQuery: String, compareTo: String): Boolean {
        return getSelectedOption(cssQuery) == compareTo
    }

    /**
     * 获取当前请求结果的 Document 中指定 ID 的 select 组件所选中的 value 值
     */
    fun getSelectedOption(cssQuery: String): String {
        select(cssQuery).select("option").forEach { element ->
            if (!element.hasAttr("selected")){
                return@forEach
            }
            return element.attr("value")
        }
        return ""
    }

    fun text(): String = document.text()
    fun html(): String = document.html()
}