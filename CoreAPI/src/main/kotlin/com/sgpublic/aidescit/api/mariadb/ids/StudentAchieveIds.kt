package com.sgpublic.aidescit.api.mariadb.ids

import java.io.Serializable

/**
 * 数据表 student_achieve 多主键封装
 */
class StudentAchieveIds: Serializable {
    var username: String = ""

    var year: String = ""

    var semester: Short = 0
}