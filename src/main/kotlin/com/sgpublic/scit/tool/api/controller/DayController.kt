package com.sgpublic.scit.tool.api.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DayController {

    @RequestMapping("/api/day")
    @Throws(Exception::class)
    fun day(): String {
        return ""
    }
}