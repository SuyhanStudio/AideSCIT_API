package com.sgpublic.aidescit.api.manager

import com.sgpublic.aidescit.api.exceptions.InvalidSignException
import com.sgpublic.aidescit.api.mariadb.dao.SignRepository
import com.sgpublic.aidescit.api.core.util.ArgumentReader
import org.springframework.beans.factory.annotation.Autowired
import java.security.MessageDigest

object SignManager {
    @Autowired
    private lateinit var sign: SignRepository

    fun calculate(map: Map<String, Array<String>>){
        if (!map.containsKey("sign")){
            return
        }
        val sortedMap = ArgumentReader.readRequestMap(map)
        val string = StringBuilder()
        for ((key, value) in sortedMap) {
            if (string.isNotEmpty()){
                string.append("&")
            }
            string.append("$key=$value")
        }

        try {
            val appSecret = sign.getAppSecret(
                (sortedMap["app_key"] ?: sign.getAppKey()) as String,
                (sortedMap["platform"] ?: SignRepository.PLATFORM_WEB) as String
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
}