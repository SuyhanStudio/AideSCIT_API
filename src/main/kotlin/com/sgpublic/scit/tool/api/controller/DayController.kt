package com.sgpublic.scit.tool.api.controller

import com.sgpublic.scit.tool.api.module.DayModule
import com.sgpublic.scit.tool.api.result.SuccessResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat
import java.util.*

@RestController
class DayController {
    @RequestMapping("/scit/edutool/day")
    fun day(): Map<String, Any> {
        return DayModule.getDay()
    }
}