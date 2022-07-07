package io.github.sgpublic.aidescit.api.controller

import io.github.sgpublic.aidescit.api.module.DayModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DayController {
    @Autowired
    private lateinit var day: DayModule

    @RequestMapping("/aidescit/day")
    fun day(sign: String): Map<String, Any?> {
        return day.get()
    }
}