package io.github.sgpublic.aidescit.api.core.util

import io.github.sgpublic.aidescit.api.Application
import io.github.sgpublic.aidescit.api.exceptions.InvalidSignException
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import io.github.sgpublic.aidescit.api.exceptions.ServiceExpiredException
import io.github.sgpublic.aidescit.api.mariadb.dao.SignKeysRepository
import io.github.sgpublic.aidescit.api.module.APIModule
import okhttp3.internal.toLongOrDefault
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

/**
 * sign 计算工具封装
 */
@Component
class SignUtil {
    companion object {
        const val PLATFORM_WEB = "web"
        const val PLATFORM_ANDROID = "android"

        private lateinit var sign: SignKeysRepository

        @DependsOn("signKeysRepository")
        fun calculate(map: Map<String, Array<String>>){
            if (!map.containsKey("sign")){
                Log.d("未提交 sign")
                return
            }
            val sortedMap = ArgumentReader.readRequestMap(map)
            val string = StringBuilder()
            for ((key, value) in sortedMap) {
                if (key == "sign"){
                    continue
                }
                if (string.isNotEmpty()){
                    string.append("&")
                }
                string.append("$key=$value")
            }

            if (!Application.DEBUG){
                val ts = sortedMap["ts"].toString()
                    .toLongOrDefault(0)
                    .minus(APIModule.TS)
                if (ts > 600 || ts < -30){
                    throw ServiceExpiredException()
                }
            }

            val appSecret = sign.getAppSecret(
                (sortedMap["app_key"] ?: sign.getAppKey()!!),
                (sortedMap["platform"] ?: PLATFORM_WEB)
            ) ?: throw ServerRuntimeException.INTERNAL_ERROR
            string.append(appSecret)
            val sign = MD5Util.encodeFull(string.toString())
            if (sign == sortedMap["sign"]){
                return
            }
            throw InvalidSignException(sign, sortedMap["sign"]!!)
        }
    }

    @Autowired
    fun setSignKeysRepository(sign: SignKeysRepository){
        Companion.sign = sign
    }
}