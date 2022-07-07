package io.github.sgpublic.aidescit.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EnumClassHashCode {
    enum class TokenType {
        ACCESS, REFRESH
    }

    @Test
    fun testHashCode() {
        val code = TokenType.ACCESS.hashCode()
        assert(code.toString(16) == "4512f5f1")
    }
}