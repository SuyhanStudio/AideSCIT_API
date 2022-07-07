package io.github.sgpublic.aidescit.api.data

import io.github.sgpublic.aidescit.api.core.spring.property.SemesterInfoProperty
import java.io.Serializable

/**
 * 可选参数封装
 * @param year 学年，默认为 [SemesterInfoProperty.YEAR]
 * @param semester 学期，默认为 [SemesterInfoProperty.SEMESTER]
 */
@Suppress("KDocUnresolvedReference")
class SemesterInfo: Serializable {
    var year: String = SemesterInfoProperty.YEAR
    var semester: Short = SemesterInfoProperty.SEMESTER
}