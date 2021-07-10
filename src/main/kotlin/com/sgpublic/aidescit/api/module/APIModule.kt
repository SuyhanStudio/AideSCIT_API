package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.Cookies
import okhttp3.*
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

    /** 获取当前时间戳 */
    @JvmStatic
    val TS: Long get() = System.currentTimeMillis() / 1000

    /**
     * 创建请求 [Call]
     * @param url 请求地址
     * @param body 请求表单，若留空则强制使用 GET 请求
     * @param headers 附带请求头，可空
     * @param cookies 附带 Cookie，可空
     * @param method 请求方法，留空则默认选择 POST 请求
     * @return 返回 [Call]
     */
    @JvmStatic
    fun buildRequest(url: String, body: FormBody? = null, headers: Headers? = null,
                     cookies: Cookies? = null, method: Int = METHOD_POST): Call {
        val request = Request.Builder()
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
            }
        }
        request.url(urlFinal.toString())
        headers?.let {
            request.headers(it)
        }
        cookies?.let {
            request.addHeader("Cookie", it.toString())
        }
        return client.newCall(request.build())
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
}