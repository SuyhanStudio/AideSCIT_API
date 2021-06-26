package com.sgpublic.scit.tool.api.manager

import com.sgpublic.scit.tool.api.exceptions.InvalidSignException
import com.sgpublic.scit.tool.api.util.ArgumentReader
import java.security.MessageDigest

object SignManager {
    fun calculate(map: Map<String, Array<String>>){
        val sortedMap = ArgumentReader.readRequestMap(map)
        if (!sortedMap.containsKey("sign")){
            return
        }
        val string = StringBuilder()
        for ((key, value) in sortedMap) {
            if (string.isNotEmpty()){
                string.append("&")
            }
            string.append("$key=$value")
        }

        try {
            val appSecret = getAppSecretWithAppKey(
                (sortedMap["app_key"] ?: getDefaultAppKey()) as String
            )
            string.append(appSecret)
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val sign = instance.digest(string.toString().toByteArray()).contentToString()
            if (sign == sortedMap["sign"]){
                return
            }
        } catch (e: IndexOutOfBoundsException){ }
        throw InvalidSignException()
    }

    private fun getDefaultAppKey(): String {
        return ""
    }

    private fun getAppSecretWithAppKey(appKey: String): String {
        return ""
    }
}