package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.util.AdvJSONObject
import com.sgpublic.aidescit.api.data.ViewStateDocument
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import okhttp3.*
import okio.IOException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

/** 请求操作模块 */
object APIModule {
    /** 创建请求客户端 */
    @JvmStatic
    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        readTimeout(5, TimeUnit.SECONDS)
        writeTimeout(5, TimeUnit.SECONDS)
        connectTimeout(10, TimeUnit.SECONDS)
        callTimeout(5, TimeUnit.MINUTES)
        followRedirects(false)
        followSslRedirects(false)
    }.build()

    /** 请求方法 GET */
    @JvmStatic
    val METHOD_GET: Int = 0
    /** 请求方法 POST */
    @JvmStatic
    val METHOD_POST: Int = 1

    /**
     * 请求附带 Cookie 键
     */
    @JvmStatic
    val COOKIE_KEY: String = "ASP.NET_SessionId"

    /**
     * 请求附带 Referer 键
     */
    @JvmStatic
    val REFERER_KEY: String = "Referer"

    /** 获取当前时间戳 */
    @JvmStatic
    val TS: Long get() = System.currentTimeMillis() / 1000

    /**
     * 创建请求并执行
     * @param url 请求地址
     * @param body 请求表单，若留空则强制使用 GET 请求
     * @param headers 附带请求头，可空
     * @param cookies 附带 Cookie，可空
     * @param method 请求方法，留空则默认选择 POST 请求
     * @return 返回 [Response]
     * @throws IOException if the request could not be executed due to cancellation, a connectivity problem or timeout. Because networks can fail during an exchange, it is possible that the remote server accepted the request before the failure.
     * @throws IllegalStateException when the call has already been executed.
     */
    @JvmStatic
    @Throws(IOException::class, IllegalStateException::class)
    fun executeResponse(url: String, body: FormBody? = null, headers: Headers? = null,
                        cookies: Cookies? = null, method: Int = METHOD_GET): Response {
        val request = Request.Builder()
        headers?.let {
            request.headers(it)
        }
        cookies?.let {
            request.addHeader("Cookie", it.toString())
        }
        val urlFinal = StringBuilder(url)
        body?.let {
            if (method == METHOD_GET){
                urlFinal.append("?")
                for (index in 0 until it.size){
                    if (index != 0){
                        urlFinal.append("&")
                    }
                    urlFinal.append("${body.name(index)}=${body.value(index)}")
                }
            } else if (method == METHOD_POST){
                request.post(it)
                request.addHeader("Content-Type", "application/x-www-form-urlencoded")
            }
        }
        request.url(urlFinal.toString())

        client.newCall(request.build()).execute().let {
            return it
        }
    }

    /**
     * 创建请求并执行，返回 [ViewStateDocument]，可选检查 __VIEWSTATE
     * @see APIModule.executeResponse
     * @return 返回 [ViewStateDocument]
     * @throws ServerRuntimeException 当网络请求失败或检查 __VIEWSTATE 未发现时抛出。
     */
    @Throws(IOException::class, IllegalStateException::class, ServerRuntimeException::class)
    fun executeDocument(
        url: String, body: FormBody? = null, headers: Headers? = null,
        cookies: Cookies? = null, method: Int = METHOD_GET
    ): ViewStateDocument {
        executeResponse(url, body, headers, cookies, method).body?.string().run {
            if (this == null){
                throw ServerRuntimeException.NETWORK_FAILED
            }
            return ViewStateDocument(url, Jsoup.parse(this), cookies, headers)
        }
    }

    /**
     * 创建请求并执行，返回 [JSONObject]
     * @see APIModule.executeResponse
     * @return 返回 [JSONObject]
     * @throws ServerRuntimeException 当网络请求失败时抛出。
     */
    @Throws(IOException::class, IllegalStateException::class, ServerRuntimeException::class)
    fun executeJSONObject(url: String, body: FormBody? = null, headers: Headers? = null,
                        cookies: Cookies? = null, method: Int = METHOD_GET): AdvJSONObject {
        return executeResponse(url, body, headers, cookies, method).body?.string()?.run {
            return@run AdvJSONObject(this)
        } ?: throw ServerRuntimeException.NETWORK_FAILED
    }

    /**
     * 创建请求表单
     * @param pairs 表单键值对
     * @return 返回 [FormBody]
     */
    @JvmStatic
    fun buildFormBody(vararg pairs: Pair<String, Any>): FormBody {
        return FormBody.Builder().run {
            for ((key, value) in pairs) {
                add(key, value.toString())
            }
            build()
        }
    }

    /**
     * 创建请求头
     * @param pairs 请求头键值对
     * @return 返回 [Headers]
     */
    @JvmStatic
    fun buildHeaders(vararg pairs: Pair<String, Any>): Headers {
        return Headers.Builder().apply {
            for ((key, value) in pairs) {
                add(key, value.toString())
            }
        }.build()
    }

    /**
     * 创建请求头中的 Cookie 参数
     * @param pairs Cookie 键值对
     * @return 返回 [Cookies]
     */
    @JvmStatic
    fun buildCookies(vararg pairs: Pair<String, Any>): Cookies {
        return Cookies.Builder().apply {
            for ((key, value) in pairs) {
                add(key, value.toString())
            }
        }.build()
    }

    /**
     * Cookies 对象封装
     */
    class Cookies private constructor(private val cookies: Map<String, ArrayList<Any>>){
        override fun toString(): String {
            return StringBuilder().apply {
                for ((key, values) in cookies){
                    for (value in values) {
                        append("$key=$value; ")
                    }
                }
            }.toString()
        }

        class Builder {
            private val cookies: MutableMap<String, ArrayList<Any>> = mutableMapOf()

            fun add(key: String, value: Any){
                if (cookies[key] == null){
                    cookies[key] = ArrayList()
                }
                cookies[key]?.add(value)
            }

            fun build() = Cookies(cookies)
        }
    }
}