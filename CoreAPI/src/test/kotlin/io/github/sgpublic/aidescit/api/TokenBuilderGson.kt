package io.github.sgpublic.aidescit.api

import com.google.gson.Gson
import io.github.sgpublic.aidescit.api.core.util.TokenUtil
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TokenBuilderGson {
    private val gson: String get() = Gson().toJson(
        TokenUtil.Companion.TokenBuilder().also {
            it.type = TokenUtil.Companion.TokenBuilder.ACCESS
        }
    )

    @Test
    fun test() {
        println(gson)
        assert(gson == gson)
        assert(gson == gson)
        assert(gson == gson)
    }
}