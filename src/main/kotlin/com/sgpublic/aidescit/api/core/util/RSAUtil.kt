package com.sgpublic.aidescit.api.core.util

import com.sgpublic.aidescit.api.core.spring.property.KeyProperty
import javax.crypto.Cipher

object RSAUtil {
    @JvmStatic
    private val cp: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

    init {
        cp.init(Cipher.ENCRYPT_MODE, KeyProperty.PRIVATE_KEY)
    }

    fun decode(src: String): String {
        return cp.doFinal(src.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
    }
}