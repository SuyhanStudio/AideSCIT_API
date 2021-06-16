package com.sgpublic.scit.tool.api.data

data class BaseRequestData(
    val ts: Long,
    val sign: String,
    val build: Long = -1,
    val platform: String = "web",
    val app_key: String
)
