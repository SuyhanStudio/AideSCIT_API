package io.github.sgpublic.aidescit.api.module

import io.github.sgpublic.aidescit.api.core.util.jsonBody
import io.github.sgpublic.aidescit.api.mariadb.dao.HitokotoRepository
import io.github.sgpublic.aidescit.api.mariadb.domain.Hitokoto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * 每日一句模块
 */
@Component
class HitokotoModule {
    @Autowired
    private lateinit var hitokoto: HitokotoRepository

    /**
     * 获取一个 hitokoto
     */
    fun get(): Hitokoto {
        return if (hitokoto.tryGet() == null){
            refresh()
        } else {
            hitokoto.randGet()
        }
    }

    /** 从 v1.hitokoto.cn 获取新的 hitokoto */
    private fun refresh(): Hitokoto {
        val json = APIModule.executeResponse(
            url = "https://v1.hitokoto.cn/",
            body = APIModule.buildFormBody(
                "encode" to "json"
            ),
            method = APIModule.METHOD_GET
        ).jsonBody(Hitokoto::class.java)

        try {
            if (hitokoto.getByIndex(json.id) == null){
                hitokoto.save(json)
            }
        } finally { }
        return hitokoto.randGet()
    }
}