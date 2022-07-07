package io.github.sgpublic.aidescit.api.core.util

import java.nio.charset.Charset
import java.util.*

/**
 * Base64 简单封装
 */
object Base64Util {
    /**
     * 加密到 [ByteArray]
     * @param src 传入 [ByteArray] 类型
     * @return 返回 [ByteArray]
     */
    fun encode(src: ByteArray): ByteArray {
        return Base64.getEncoder().encode(src)
    }

    /**
     * 加密到 [ByteArray]
     * @param src 传入 [String] 类型字符串
     * @return 返回 [ByteArray]
     */
    fun encode(src: String, charset: Charset = Charsets.UTF_8): ByteArray {
        return encode(src.toByteArray(charset))
    }

    /**
     * 加密到 [ByteArray]
     * @param src 传入 [StringBuilder] 类型
     * @return 返回 [ByteArray]
     */
    fun encode(src: StringBuilder, charset: Charset = Charsets.UTF_8): ByteArray {
        return encode(src.toString(), charset)
    }

    /**
     * 加密到 [String]
     * @param src 传入 [ByteArray] 类型
     * @return 返回 [String]
     */
    fun encodeToString(src: ByteArray): String {
        return Base64.getEncoder().encodeToString(src)
    }

    /**
     * 加密到 [String]
     * @param src 传入 [String] 类型
     * @return 返回 [String]
     */
    fun encodeToString(src: String, charset: Charset = Charsets.UTF_8): String {
        return encodeToString(src.toByteArray(charset))
    }

    /**
     * 加密到 [String]
     * @param src 传入 [StringBuilder] 类型
     * @return 返回 [String]
     */
    fun encodeToString(src: StringBuilder, charset: Charset = Charsets.UTF_8): String {
        return encodeToString(src.toString().toByteArray(charset))
    }

    /**
     * 解密到 [ByteArray]
     * @param src 传入 [String] 类型
     * @return 返回 [String]
     */
    fun decode(src: String): ByteArray {
        return Base64.getDecoder().decode(src)
    }

    /**
     * 解密到 [String]
     * @param src 传入 [String] 类型
     * @return 返回 [String]
     */
    fun decodeToString(src: String, charset: Charset = Charsets.UTF_8): String {
        return decode(src).toString(charset)
    }
}