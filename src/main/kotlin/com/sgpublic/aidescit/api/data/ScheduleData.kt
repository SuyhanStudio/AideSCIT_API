package com.sgpublic.aidescit.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.sgpublic.aidescit.api.data.ScheduleData.Companion.ScheduleDay
import org.json.JSONArray
import org.json.JSONObject

/**
 * 课表数据，每天的课程封装为 [ScheduleDay]
 * @param sunday 周日
 * @param monday 周一
 * @param tuesday 周二
 * @param wednesday 周三
 * @param thursday 周四
 * @param friday 周五
 * @param saturday 周六
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ScheduleData(
    val sunday: ScheduleDay = ScheduleDay(),
    val monday: ScheduleDay = ScheduleDay(),
    val tuesday: ScheduleDay = ScheduleDay(),
    val wednesday: ScheduleDay = ScheduleDay(),
    val thursday: ScheduleDay = ScheduleDay(),
    val friday: ScheduleDay = ScheduleDay(),
    val saturday: ScheduleDay = ScheduleDay()
) {
    override fun toString(): String {
        return JSONObject().apply {
            if (sunday.isNotEmpty()){
                put("sunday", JSONObject(sunday.toString()))
            }
            if (monday.isNotEmpty()){
                put("monday", JSONObject(monday.toString()))
            }
            if (tuesday.isNotEmpty()){
                put("tuesday", JSONObject(tuesday.toString()))
            }
            if (wednesday.isNotEmpty()){
                put("wednesday", JSONObject(wednesday.toString()))
            }
            if (thursday.isNotEmpty()){
                put("thursday", JSONObject(thursday.toString()))
            }
            if (friday.isNotEmpty()){
                put("friday", JSONObject(friday.toString()))
            }
            if (saturday.isNotEmpty()){
                put("saturday", JSONObject(saturday.toString()))
            }
        }.toString()
    }

    companion object {
        /**
         * 单日课程
         * @param am1 第 1-2 节，即上午第一节
         * @param am2 第 3-4 节，即上午第二节
         * @param pm1 第 5-6 节，即下午第一节
         * @param pm2 第 7-8 节，即下午第二节
         * @param ev 第 9-10 节，即晚上第一节
         */
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        data class ScheduleDay(
            val am1: ScheduleItemGroup = ScheduleItemGroup(),
            val am2: ScheduleItemGroup = ScheduleItemGroup(),
            val pm1: ScheduleItemGroup = ScheduleItemGroup(),
            val pm2: ScheduleItemGroup = ScheduleItemGroup(),
            val ev: ScheduleItemGroup = ScheduleItemGroup()
        ) {
            override fun toString(): String {
                return JSONObject().apply {
                    if (am1.isNotEmpty()){
                        put("am1", JSONArray().apply {
                            am1.forEach {
                                put(JSONObject(it.toString()))
                            }
                        })
                    }
                    if (am2.isNotEmpty()){
                        put("am2", JSONArray().apply {
                            am2.forEach {
                                put(JSONObject(it.toString()))
                            }
                        })
                    }
                    if (pm1.isNotEmpty()){
                        put("pm1", JSONArray().apply {
                            pm1.forEach {
                                put(JSONObject(it.toString()))
                            }
                        })
                    }
                    if (pm2.isNotEmpty()){
                        put("pm2", JSONArray().apply {
                            pm2.forEach {
                                put(JSONObject(it.toString()))
                            }
                        })
                    }
                    if (ev.isNotEmpty()){
                        put("ev", JSONArray().apply {
                            ev.forEach {
                                put(JSONObject(it.toString()))
                            }
                        })
                    }
                }.toString()
            }

            @JsonIgnore
            fun isNotEmpty(): Boolean {
                return am1.size + am2.size + pm1.size + pm2.size + ev.size > 0
            }
        }

        class ScheduleItemGroup: ArrayList<ScheduleItem>()

        /**
         * 单个课程信息
         * @param name 课程名称
         * @param room 上课位置
         * @param teacher 授课教师姓名
         * @param range 课程名称
         */
        data class ScheduleItem(
            var name: String = "",
            var room: String = "",
            var teacher: String = "",
            val range: ArrayList<Short> = arrayListOf()
        ) {
            override fun toString(): String {
                return JSONObject()
                    .put("name", name)
                    .put("room", room)
                    .put("teacher", teacher)
                    .put("range", JSONArray().apply {
                        range.forEach {
                            put(it)
                        }
                    })
                    .toString()
            }

            override fun equals(other: Any?): Boolean {
                if (other !is ScheduleItem){
                    return false
                }
                if (other.name != name){
                    return false
                }
                if (other.room != room){
                    return false
                }
                if (other.teacher != teacher){
                    return false
                }
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + room.hashCode()
                result = 31 * result + teacher.hashCode()
                return result
            }
        }
    }
}