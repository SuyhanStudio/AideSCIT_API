package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class DayModule {
    fun getDay(): Map<String, Any> {
        val timeNow = Calendar.getInstance().timeInMillis
        val timeBetween = (timeNow - SemesterInfoProperty.START.timeInMillis) / 86400000
        val sdf = SimpleDateFormat.getDateInstance()

        return SuccessResult(
            "day_count" to timeBetween,
            "date" to sdf.format(SemesterInfoProperty.START.time),
            "semester" to SemesterInfoProperty.SEMESTER,
            "school_year" to SemesterInfoProperty.YEAR,
            "evaluation" to SemesterInfoProperty.EVALUATION
        )
    }
}