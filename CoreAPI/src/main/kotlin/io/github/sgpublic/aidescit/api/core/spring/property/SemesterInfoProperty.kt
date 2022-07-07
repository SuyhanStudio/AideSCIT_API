package io.github.sgpublic.aidescit.api.core.spring.property

import io.github.sgpublic.aidescit.api.core.util.Log
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*
import kotlin.properties.Delegates

/**
 * 注入 semester.properties
 */
@Component
@ConfigurationProperties(prefix = "aidescit.semester")
class SemesterInfoProperty {
    companion object {
        private var semester by Delegates.notNull<Short>()
        private lateinit var year: String
        private var scheduleCanInquire: Boolean = true
        private var evaluation: Boolean = false
        private lateinit var start: Calendar

        @JvmStatic
        val SEMESTER: Short get() = semester
        @JvmStatic
        val YEAR: String get() = year
        @JvmStatic
        val SCHEDULE_CAN_INQUIRE: Boolean get() = scheduleCanInquire
        @JvmStatic
        val EVALUATION: Boolean get() = evaluation
        @JvmStatic
        val START: Calendar get() = start
    }

    fun setNum(value: Short) {
        semester = value
    }

    fun setYear(value: String) {
        year = value
    }

    fun setEvaluation(value: Boolean) {
        evaluation = value
    }

    fun setStart(value: String) {
        try {
            val time = value.split("/")
            start = Calendar.getInstance().apply {
                set(time[0].toInt(), time[1].toInt() - 1, time[2].toInt())
            }
        } catch (e: Exception){
            Log.f("请将 aidescit.semester.start 设置为正确的日期格式")
        }
    }
}