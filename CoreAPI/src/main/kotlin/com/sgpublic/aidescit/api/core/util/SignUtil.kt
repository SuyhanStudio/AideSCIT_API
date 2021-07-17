package com.sgpublic.aidescit.api.core.util

import com.sgpublic.aidescit.api.exceptions.InvalidSignException
import com.sgpublic.aidescit.api.exceptions.ServiceExpiredException
import com.sgpublic.aidescit.api.mariadb.dao.SignKeysRepository
import com.sgpublic.aidescit.api.module.APIModule
import okhttp3.internal.toLongOrDefault
import org.springframework.beans.factory.annotation.Autowired
import java.security.MessageDigest

/**
 * sign 计算工具封装
 */
object SignUtil {
    @Autowired
    private lateinit var sign: SignKeysRepository

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

        val ts = sortedMap["ts"].toString()
            .toLongOrDefault(0)
            .minus(APIModule.TS)
        if (ts > 600 || ts < -30){
            throw ServiceExpiredException()
        }

        val appSecret = sign.getAppSecret(
            (sortedMap["app_key"] ?: sign.getAppKey()) as String,
            (sortedMap["platform"] ?: SignKeysRepository.PLATFORM_WEB) as String
        )
        string.append(appSecret)
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val sign = instance.digest(string.toString().toByteArray()).contentToString()
        if (sign != sortedMap["sign"]){
            throw InvalidSignException()
        }
    }
}