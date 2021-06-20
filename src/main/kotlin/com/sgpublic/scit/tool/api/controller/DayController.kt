package com.sgpublic.scit.tool.api.controller

import com.sgpublic.scit.tool.api.core.Log
import com.sgpublic.scit.tool.api.module.DayModule
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DayController {
    @RequestMapping("/scit/edutool/day")
    fun day(): Map<String, Any> {
        Log.d("测试输出")
        return DayModule.getDay()
    }
}