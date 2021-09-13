package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.Application
import com.sgpublic.aidescit.api.core.spring.property.KeyProperty
import com.sgpublic.aidescit.api.core.util.*
import com.sgpublic.aidescit.api.data.ViewStateDocument
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import kotlin.math.pow

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
     * 请求附带 Referer 键
     */
    @JvmStatic
    val REFERER: String = "Referer"
    /**
     * 请求附带 Content-Type 键
     */
    @JvmStatic
    val CONTENT_TYPE: String = "Content-Type"

    /** 获取当前时间戳，秒 */
    @JvmStatic
    val TS: Long get() = TS_FULL / 1000
    /** 获取当前时间戳，毫秒 */
    @JvmStatic
    val TS_FULL: Long get() = System.currentTimeMillis()

    val NONCE: Long get() = (10.0.pow(13) * (Math.random() + 1)).toLong()

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
    fun executeResponse(url: String, body: RequestBody? = null, headers: Headers? = null,
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
            if (it is FormBody) {
                if (method == METHOD_GET){
                    urlFinal.append("?")
                    for (index in 0 until it.size){
                        if (index != 0){
                            urlFinal.append("&")
                        }
                        urlFinal.append("${it.name(index)}=${it.value(index)}")
                    }
                } else {
                    request.post(it)
                    request.addHeader("Content-Type", "application/x-www-form-urlencoded")
                }
            } else {
                if (method == METHOD_GET){
                    urlFinal.append("?$it")
                } else {
                    request.post(it)
                    request.addHeader("Content-Type", "application/json;charset=UTF-8")
                }
            }
        }
        request.url(urlFinal.toString())
        if (Application.DEBUG){
            val log = StringBuilder("网络请求\n")
                .append(if (method == METHOD_POST) "POST " else "GET ")
                .append(url)
            headers?.let {
                log.append("\n[header]")
                it.forEach {
                    log.append("\n  ${it.first}: ${it.second}")
                }
            }
            cookies?.let {
                if (headers == null) {
                    log.append("\n[header]")
                }
                log.append("\n  cookie: $it")
            }
            body?.let {
                log.append("\n[body]")
                if (it is FormBody){
                    for (index in 0 until it.size) {
                        log.append("\n  ${it.name(index)}=${it.value(index)}")
                    }
                } else {
                    log.append("\n  $it")
                }
            }
            Log.t(log.toString())
        }

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
     * 创建 Form 表单
     * @param pairs 表单键值对
     * @return 返回 [FormBody]
     */
    @JvmStatic
    fun buildFormBody(vararg pairs: Pair<String, Any?>): FormBody {
        return FormBody.Builder().run {
            for ((key, value) in pairs) {
                add(key, value.toString())
            }
            build()
        }
    }

    /**
     * 创建 JSON 表单
     * @param pairs 表单键值对
     * @return 返回 [FormBody]
     */
    @JvmStatic
    fun buildJsonBody(vararg pairs: Pair<String, Any?>): RequestBody {
        return advMapOf(*pairs).toString().toRequestBody(
            "application/json".toMediaType()
        )
    }

    /**
     * 创建请求表单
     * @param map 表单键值对
     * @return 返回 [FormBody]
     */
    @JvmStatic
    fun buildFormBody(map: LinkedHashMap<String, Any?>): FormBody {
        return FormBody.Builder().run {
            for ((key, value) in map) {
                add(key, value.toString())
            }
            build()
        }
    }

    /**
     * 创建 JSON 表单
     * @param map 表单键值对
     * @return 返回 [FormBody]
     */
    @JvmStatic
    fun buildJsonBody(map: LinkedHashMap<String, Any?>): RequestBody {
        return map.toString().toRequestBody(
            "application/json".toMediaType()
        )
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
        companion object {
            /**
             * 请求附带 Cookie 键
             */
            @JvmStatic
            val SESSION_ID: String = "ASP.NET_SessionId"
        }

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

    @JvmStatic
    fun getSecretParam(data: AdvMap){
        val dataString = data.toSortedString()
        var index = 0
        val result = ArrayList<String>()
        while (true) {
            val start = index * 30
            if (start > dataString.length){
                break
            }
            val end = if (dataString.length < ++index * 30) {
                dataString.length
            } else { index * 30 }
            val subString = dataString.substring(start, end)
            result.add(RSAUtil.encode(subString, secretPublicKey))
        }
        data.put("secretParam" to result.toJsonString())
    }

    private val secretPublicKey: Cipher get() {
        val cp = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        val key = KeyFactory.getInstance("RSA")
        cp.init(Cipher.ENCRYPT_MODE, key.generatePublic(
            X509EncodedKeySpec(Base64Util.decode(
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDACwPDxYycdCiNeblZa9LjvDzb" +
                    "iZU1vc9gKRcG/pGjZ/DJkI4HmoUE2r/o6SfB5az3s+H5JDzmOMVQ63hD7LZQGR4k" +
                    "3iYWnCg3UpQZkZEtFtXBXsQHjKVJqCiEtK+gtxz4WnriDjf+e/CxJ7OD03e7sy5N" +
                    "Y/akVmYNtghKZzz6jwIDAQAB"
            ))
        ))
        return cp
    }
}

fun ArrayList<String>.toJsonString(): String {
    return JSONArray(this).toString()
}

fun Response.jsonBody(): JSONObject {
    val result = this.body?.string() ?: throw ServerRuntimeException.NETWORK_FAILED
    return JSONObject(result)
}