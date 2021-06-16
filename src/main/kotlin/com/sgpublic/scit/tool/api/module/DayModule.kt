package com.sgpublic.scit.tool.api.module

import com.sgpublic.scit.tool.api.data.BaseRequestData
import com.sgpublic.scit.tool.api.results.ResultSuccess
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.util.*

@Component
class DayModule {
    companion object {
        private const val SEMESTER = 2
        private const val SCHOOL_YEAR = "2020-2021"
        private const val EVALUATION = false
        private val DATE_START = Date(1614441600000)
    }

    fun getDay(): String {
        val timeNow = Date()
        return ResultSuccess()
            .put("semester", SEMESTER)
            .put("school_year", SCHOOL_YEAR)
            .put("evaluation", EVALUATION)
            .toString()
    }
}
