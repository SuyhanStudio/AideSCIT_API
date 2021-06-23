package com.sgpublic.scit.tool.api.controller

import com.sgpublic.scit.tool.api.module.DayModule
import com.sgpublic.scit.tool.api.util.Log
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DayController {
    @RequestMapping("/scit/edutool/day")
    fun day(): Map<String, Any> {
        Log.d("test", "201940010074")
        return DayModule.getDay()
    }
}