package com.sgpublic.aidescit.api.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.sgpublic.aidescit.api.core.util.AdvMap
import com.sgpublic.aidescit.api.data.ScheduleData.Companion.ScheduleDay
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
@Suppress("KDocUnresolvedReference")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ScheduleData: AdvMap() {
    val sunday: ScheduleDay get() = getSet("sunday", ScheduleDay())
    val monday: ScheduleDay get() = getSet("monday", ScheduleDay())
    val tuesday: ScheduleDay get() = getSet("tuesday", ScheduleDay())
    val wednesday: ScheduleDay get() = getSet("wednesday", ScheduleDay())
    val thursday: ScheduleDay get() = getSet("thursday", ScheduleDay())
    val friday: ScheduleDay get() = getSet("friday", ScheduleDay())
    val saturday: ScheduleDay get() = getSet("saturday", ScheduleDay())

    override fun toString(): String {
        return JSONObject(this).toString()
    }

    companion object {
        /**
         * 单日课程，每个位置上的课程封装为 [ScheduleItemGroup]
         * @param am1 第 1-2 节，即上午第一节
         * @param am2 第 3-4 节，即上午第二节
         * @param pm1 第 5-6 节，即下午第一节
         * @param pm2 第 7-8 节，即下午第二节
         * @param ev 第 9-10 节，即晚上第一节
         */
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        class ScheduleDay: AdvMap() {
            val am1: ScheduleItemGroup get() = getSet("am1", ScheduleItemGroup())
            val am2: ScheduleItemGroup get() = getSet("am2", ScheduleItemGroup())
            val pm1: ScheduleItemGroup get() = getSet("pm1", ScheduleItemGroup())
            val pm2: ScheduleItemGroup get() = getSet("pm2", ScheduleItemGroup())
            val ev: ScheduleItemGroup get() = getSet("ev", ScheduleItemGroup())
        }

        /**
         * 单个位置的课程组合，用于应对单个位置有多个课程安排的情况
         */
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
            override fun equals(other: Any?): Boolean {
                if (other !is ScheduleItem){
                    return false
                }
                if (other.name != name || other.room != room || other.teacher != teacher){
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