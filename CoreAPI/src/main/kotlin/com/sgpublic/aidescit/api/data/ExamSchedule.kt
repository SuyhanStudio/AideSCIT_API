package com.sgpublic.aidescit.api.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ExamSchedule(
    var name: String = "",
    var time: String = "",
    var location: String = "",
    @JsonProperty("set_num") var setNum: Short = 0,
)