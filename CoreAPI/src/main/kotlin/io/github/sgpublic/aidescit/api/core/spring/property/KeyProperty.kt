package io.github.sgpublic.aidescit.api.core.spring.property

import io.github.sgpublic.aidescit.api.core.util.Base64Util
import io.github.sgpublic.aidescit.api.core.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.system.exitProcess

/**
 * 注入 key.properties
 */
@Component
@ConfigurationProperties(prefix = "aidescit.rsa")
class KeyProperty {
    companion object {
        private lateinit var privateKey: PrivateKey
        private lateinit var publicKeyStr: String
        private lateinit var publicKey: PublicKey

        @JvmStatic
        val PRIVATE_KEY: PrivateKey get() = privateKey
        val PUBLIC_KEY_STRING: String get() = publicKeyStr
        val PUBLIC_KEY: PublicKey get() = publicKey
    }

    fun setPrivate(value: String){
        try {
            val decoded = Base64Util.decodeToString(value).run {
                val str = substring(27, length - 25)
                    .replace("\n", "")
                return@run Base64Util.decode(str)
            }
            val key = KeyFactory.getInstance("RSA")
            privateKey = key.generatePrivate(PKCS8EncodedKeySpec(decoded))
            return
        } catch (e: IllegalArgumentException){
            Log.e("请将 aidescit.rsa.private 设置为经 Base64 加密后的字符串", e)
        } catch (e: InvalidKeySpecException){
            Log.e("请将 aidescit.rsa.private 设置为正确的 RSA 私钥", e)
        }
        exitProcess(0)
    }

    fun setPublic(value: String){
        try {
            val decoded = Base64Util.decodeToString(value).let {
                publicKeyStr = it
                val str = it.substring(27, it.length - 25)
                    .replace("\n", "")
                return@let Base64Util.decode(str)
            }
            val key = KeyFactory.getInstance("RSA")
            publicKey = key.generatePublic(X509EncodedKeySpec(decoded))
            return
        } catch (e: IllegalArgumentException){
            Log.e("请将 aidescit.rsa.public 设置为经 Base64 加密后的字符串", e)
        } catch (e: InvalidKeySpecException){
            Log.e("请将 aidescit.rsa.public 设置为正确的 RSA 共钥", e)
        }
        exitProcess(0)
    }
}