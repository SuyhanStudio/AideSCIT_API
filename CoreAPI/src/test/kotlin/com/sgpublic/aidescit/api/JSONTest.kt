package com.sgpublic.aidescit.api

import com.sgpublic.aidescit.api.core.util.advMapOf
import com.sgpublic.aidescit.api.module.APIModule

fun main() {
    val param = advMapOf(
        "universityId" to 100831,
        "appKey" to "uap-web-key",
        "timestamp" to APIModule.TS_FULL,
        "nonce" to APIModule.NONCE,
        "clientCategory" to "PC",
        "appCode" to "officeHallApplicationCode",
        "equipmentId" to "工科助手(sgpublic2002@gmail.com)",
        "equipmentVersion" to "1.0.0-alpha01",
        "loginWay" to "ACCOUNT"
    )
    println(param.toSortedString())
}