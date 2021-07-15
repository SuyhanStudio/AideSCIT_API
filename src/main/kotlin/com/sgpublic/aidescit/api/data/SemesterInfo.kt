package com.sgpublic.aidescit.api.data

import com.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import org.json.JSONObject

/**
 * 可选参数封装
 * @param year 学年，默认为 [SemesterInfoProperty.YEAR]
 * @param semester 学期，默认为 [SemesterInfoProperty.SEMESTER]
 */
@Suppress("KDocUnresolvedReference")
class SemesterInfo {
    var year: String = SemesterInfoProperty.YEAR
    var semester: Short = SemesterInfoProperty.SEMESTER

    override fun toString(): String {
        return JSONObject()
            .put("year", year)
            .put("semester", semester)
            .toString()
    }
}