package io.github.sgpublic.aidescit.api.core.util

import io.github.sgpublic.aidescit.api.core.spring.property.KeyProperty
import org.springframework.context.annotation.DependsOn
import javax.crypto.Cipher

/**
 * RSA 简单封装，支持 RSA/ECB/PKCS1Padding
 */
@DependsOn("keyProperty")
object RSAUtil {
    @JvmStatic
    private val pri: Cipher get() {
        val cp = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cp.init(Cipher.DECRYPT_MODE, KeyProperty.PRIVATE_KEY)
        return cp
    }

    /**
     * 使用私钥解密文本
     * @param src 需解密的文本
     * @return 返回解密的文本
     */
    fun decode(src: String): String {
        return decode(src, pri)
    }

    /**
     * 使用指定密钥解密文本
     * @param src 需解密的文本
     * @param cp 指定密钥
     * @return 返回解密的文本
     */
    fun decode(src: String, cp: Cipher): String {
        return cp.doFinal(Base64Util.decode(src)).toString(Charsets.UTF_8)
    }

    /**
     * 使用私钥加密文本
     * @param src 需解密的文本
     * @return 返回解密的文本
     */
    fun encode(src: String): String {
        return Base64Util.encodeToString(pri.doFinal(src.toByteArray(Charsets.UTF_8)))
    }

    /**
     * 使用指定密钥解密文本
     * @param src 需解密的文本
     * @return 返回解密的文本
     */
    fun encode(src: String, cp: Cipher): String {
        return Base64Util.encodeToString(cp.doFinal(src.toByteArray(Charsets.UTF_8)))
    }
}