package com.sgpublic.aidescit.api.controller

import com.sgpublic.aidescit.api.module.DayModule
import com.sgpublic.aidescit.api.result.SuccessResult
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