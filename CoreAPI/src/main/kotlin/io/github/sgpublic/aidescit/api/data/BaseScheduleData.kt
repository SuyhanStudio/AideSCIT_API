package io.github.sgpublic.aidescit.api.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.gson.Gson
import io.github.sgpublic.aidescit.api.data.BaseScheduleData.ScheduleDay
import java.util.*

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
abstract class BaseScheduleData<T>: Iterable<ScheduleDay<T>> {
    val sunday: ScheduleDay<T> = ScheduleDay()
    val monday: ScheduleDay<T> = ScheduleDay()
    val tuesday: ScheduleDay<T> = ScheduleDay()
    val wednesday: ScheduleDay<T> = ScheduleDay()
    val thursday: ScheduleDay<T> = ScheduleDay()
    val friday: ScheduleDay<T> = ScheduleDay()
    val saturday: ScheduleDay<T> = ScheduleDay()

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun iterator(): Iterator<ScheduleDay<T>> {
        return LinkedList<ScheduleDay<T>>().also {
            it.addAll(listOf(sunday, monday, tuesday,
                wednesday, thursday, friday, saturday))
        }.iterator()
    }

    /**
     * 单日课程，每个位置上的课程封装为 [ScheduleItemGroup]
     * @param am1 第 1-2 节，即上午第一节
     * @param am2 第 3-4 节，即上午第二节
     * @param pm1 第 5-6 节，即下午第一节
     * @param pm2 第 7-8 节，即下午第二节
     * @param ev 第 9-10 节，即晚上第一节
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class ScheduleDay<T>: Iterable<ScheduleItemGroup<T>> {
        val am1: ScheduleItemGroup<T> = ScheduleItemGroup()
        val am2: ScheduleItemGroup<T> = ScheduleItemGroup()
        val pm1: ScheduleItemGroup<T> = ScheduleItemGroup()
        val pm2: ScheduleItemGroup<T> = ScheduleItemGroup()
        val ev: ScheduleItemGroup<T> = ScheduleItemGroup()

        override fun iterator(): Iterator<ScheduleItemGroup<T>> {
            return LinkedList<ScheduleItemGroup<T>>().also {
                    it.addAll(listOf(am1, am2, pm1, pm2, ev))
            }.iterator()
        }
    }

    /**
     * 单个位置的课程组合，用于应对单个位置有多个课程安排的情况
     */
    class ScheduleItemGroup<T>: ArrayList<T>()
}

class ScheduleData: BaseScheduleData<ScheduleData.ScheduleItem>() {
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
            val range: HashSet<Short> = HashSet()
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

        override fun toString(): String {
            return "$name $room"
        }
    }
}

class RoomData: BaseScheduleData<RoomData.ClassRoomItem>() {
    /**
     * 单个空教室信息
     * @param name 课程名称
     * @param room 上课位置
     * @param teacher 授课教师姓名
     * @param range 课程名称
     */
    data class ClassRoomItem(
            var id: Int = -1,
            var room: String = "",
            val range: HashSet<Short> = HashSet()
    ) {
        override fun equals(other: Any?): Boolean {
            if (other !is ClassRoomItem){
                return false
            }
            if (other.room != room){
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return room.hashCode()
        }
    }
}