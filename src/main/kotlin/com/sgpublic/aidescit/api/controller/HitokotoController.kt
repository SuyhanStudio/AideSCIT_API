package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.HitokotoModule
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HitokotoController {
    @Autowired
    lateinit var hitokoto: HitokotoModule

    @RequestMapping("/scit/edutool/hitokoto")
    fun getHitokoto(): Map<String, Any>{
        val hitokoto = this.hitokoto.get()
        return SuccessResult(
            "hitokoto" to hitokoto.content,
            "from" to hitokoto.from
        )
    }
}