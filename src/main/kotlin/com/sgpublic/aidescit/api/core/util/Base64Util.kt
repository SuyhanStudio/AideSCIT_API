package com.sgpublic.aidescit.api.core.util

import java.util.*

object Base64Util {
    fun encode(src: String): ByteArray {
        return Base64.getEncoder().encode(src.toByteArray(Charsets.UTF_8))
    }

    fun encode(src: StringBuilder): ByteArray {
        return encode(src.toString())
    }

    fun encodeToString(src: String): String {
        return Base64.getEncoder().encodeToString(src.toByteArray(Charsets.UTF_8))
    }

    fun encodeToString(src: StringBuilder): String {
        return encodeToString(src.toString())
    }

    fun decodeToString(src: String): String {
        return decode(src).toString(Charsets.UTF_8)
    }

    fun decode(src: String): ByteArray {
        return Base64.getDecoder().decode(src)
    }
}