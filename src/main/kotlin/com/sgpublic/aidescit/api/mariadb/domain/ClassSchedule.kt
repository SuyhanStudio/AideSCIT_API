package com.sgpublic.aidescit.api.mariadb.domain

import com.sgpublic.aidescit.api.data.ScheduleData
import com.sgpublic.aidescit.api.module.APIModule
import org.json.JSONObject
import javax.persistence.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties

/**
 * 数据表 class_schedule
 */
@Entity
@Table(name = "class_schedule")
class ClassSchedule {
    @Id
    @Column(name = "t_id")
    var id: String = ""

    @Column(name = "t_faculty")
    var faculty: Int = 0

    @Column(name = "t_specialty")
    var specialty: Int = 0

    @Column(name = "t_class")
    var classId: Short = 0

    @Column(name = "t_grade")
    var grade: Short = 0

    @Column(name = "t_school_year")
    var year: String = ""

    @Column(name = "t_semester")
    var semester: Short = 0

    @Column(name = "t_content")
    var content: String = ""

    @Column(name = "t_expired")
    var expired: Long = APIModule.TS + 1296000

    @Transient
    fun isExpired() = expired < APIModule.TS

    @Transient
    fun getContent(): ScheduleData {
        JSONObject(content).run {
            val result = ScheduleData::class.createInstance()
            ScheduleData::class.declaredMemberProperties.forEach for1@{ data ->
                val dataJson = try {
                    getJSONObject(data.name)
                } catch (e: Exception){
                    return@for1
                }
                ScheduleData.Companion.ScheduleDay::class.declaredMemberProperties.forEach for2@{ day ->
                    try {
                        dataJson.getJSONArray(day.name)
                    } catch (e: Exception){
                        return@for2
                    }.forEach for3@{ position ->
                        val json = position as JSONObject
                        val entry = day.get(data.get(result) as ScheduleData.Companion.ScheduleDay)
                                as ScheduleData.Companion.ScheduleItemGroup
                        val item = ScheduleData.Companion.ScheduleItem::class.createInstance()
                        item.name = json.getString("name")
                        item.room = json.getString("room")
                        item.teacher = json.getString("teacher")
                        json.getJSONArray("range").forEach {
                            item.range.add((it as Int).toShort())
                        }
                        entry.add(item)
                    }
                }
            }
            return result
        }
    }

    @Transient
    override fun toString(): String {
        return JSONObject(this).toString()
    }
}