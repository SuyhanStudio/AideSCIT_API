package com.sgpublic.aidescit.api.module

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import com.sgpublic.aidescit.api.result.SuccessResult
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

/**
 * 学期信息模块
 */
@Component
class DayModule {
    private lateinit var result: SuccessResult
    private var expired: Long = 0

    /**
     * 获取信息
     */
    fun get(): Map<String, Any?> {
        return if (expired - APIModule.TS > 0){
            result
        } else {
            refresh()
        }
    }

    /**
     * 刷新信息
     */
    private fun refresh(): SuccessResult {
        val timeNow = Calendar.getInstance().timeInMillis
        val timeBetween = (timeNow - SemesterInfoProperty.START.timeInMillis) / 86400000
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)

        SuccessResult(
            "day_count" to timeBetween,
            "date" to sdf.format(SemesterInfoProperty.START.time),
            "semester" to SemesterInfoProperty.SEMESTER,
            "school_year" to SemesterInfoProperty.YEAR,
            "schedule_can_inquire" to SemesterInfoProperty.SCHEDULE_CAN_INQUIRE,
            "evaluation" to SemesterInfoProperty.EVALUATION
        ).let {
            expired = APIModule.TS + 1
            result = it
            return it
        }
    }
}