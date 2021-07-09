package com.sgpublic.aidescit.api.core.spring.property

import com.sgpublic.aidescit.api.core.util.Base64Util
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.X509EncodedKeySpec

@Component
@ConfigurationProperties(prefix = "aidescit.rsa")
class KeyProperty {
    companion object {
        private lateinit var privateKeySetter: PrivateKey
        @JvmStatic
        val PRIVATE_KEY: PrivateKey get() = privateKeySetter
    }

    fun setPrivate(value: String){
        val key = KeyFactory.getInstance("RSA")
        privateKeySetter = key.generatePrivate(
            X509EncodedKeySpec(Base64Util.decode(value))
        )
    }
}