package com.sgpublic.aidescit.api.core.util

import com.sgpublic.aidescit.api.core.spring.property.KeyProperty
import org.springframework.context.annotation.DependsOn
import javax.crypto.Cipher

/**
 * RSA 简单封装，支持 RSA/ECB/PKCS1Padding
 */
@DependsOn("keyProperty")
object RSAUtil {
    @JvmStatic
    private val cp: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

    init {
        cp.init(Cipher.DECRYPT_MODE, KeyProperty.PRIVATE_KEY)
    }

    /**
     * 使用私钥解密文本
     * @param src 需解密的文本
     * @return 返回解密的文本
     */
    fun decode(src: String): String {
        return cp.doFinal(src.toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
    }
}