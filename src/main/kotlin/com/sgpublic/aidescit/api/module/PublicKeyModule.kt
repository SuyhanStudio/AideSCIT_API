package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.KeyProperty
import org.springframework.stereotype.Component

@Component
class PublicKeyModule {
    fun getPublicKey(): String {
        return KeyProperty.PUBLIC_KEY
    }
}