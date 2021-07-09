package com.sgpublic.aidescit.api.core.util

import java.security.MessageDigest

object MD5Util {
    private val instance: MessageDigest = MessageDigest.getInstance("MD5")

    private fun encodeToBytes(src: String): ByteArray {
        return instance.digest(src.toByteArray())
    }

    fun encode(src: String): String {
        return encodeToBytes(src).toString()
    }

    fun encodeFull(src: String): String {
        val digest: ByteArray = encodeToBytes(src)
        return StringBuffer().run {
            for (b in digest) {
                val i :Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                append(hexString)
            }
            toString()
        }
    }
}