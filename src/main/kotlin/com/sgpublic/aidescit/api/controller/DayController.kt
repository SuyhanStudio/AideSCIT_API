package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.DayModule
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DayController {
    @RequestMapping("/aidescit/day")
    fun day(): Map<String, Any> {
        return DayModule.getDay()
    }
}