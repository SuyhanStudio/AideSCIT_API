package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.mariadb.dao.HitokotoRepository
import com.sgpublic.aidescit.api.mariadb.domain.Hitokoto
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

    /**
     * 从 v1.hitokoto.cn 获取新的 hitokoto
     */
    private fun refresh(): Hitokoto {
        val json = APIModule.executeJSONObject(
            url = "https://v1.hitokoto.cn/",
            body = APIModule.buildFormBody(
                "encode" to "json"
            ),
            method = APIModule.METHOD_GET
        )

        try {
            val result = Hitokoto()
            result.index = json.getLong("id")
            result.content = json.getString("hitokoto")
            result.type = json.getString("type")
            result.from = json.getString("from")
            result.fromWho = json.getString("from_who", "")
            result.creator = json.getString("creator")
            result.creatorUid = json.getLong("creator_uid")
            result.reviewer = json.getLong("reviewer")
            result.length = json.getLong("length")

            hitokoto.save(result)
        } finally { }
        return hitokoto.randGet()
    }
}