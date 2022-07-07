package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.core.spring.property.KeyProperty
import org.springframework.stereotype.Component

/** 处理接口 [com.sgpublic.aidescit.api.controller.PublicKeyController] */
@Component
class PublicKeyModule {
    /**
     * 获取 RSA 公钥
     */
    fun getPublicKey(): String {
        return KeyProperty.PUBLIC_KEY_STRING
    }
}