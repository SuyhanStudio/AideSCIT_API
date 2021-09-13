package com.sgpublic.aidescit.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.sgpublic.aidescit.api.core.util.AdvMap

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class AchieveData: AdvMap() {
    private val current: CurrentAchieve get() = getSet("current", CurrentAchieve())
    private val failed: FailedAchieve get() = getSet("failed", FailedAchieve())

    @JsonIgnore
    fun addCurrent(item: CurrentAchieveItem) = current.add(item)

    @JsonIgnore
    fun addFailed(item: FailedAchieveItem) = failed.add(item)

    companion object {
        class CurrentAchieve: ArrayList<CurrentAchieveItem>()

        @JsonInclude(JsonInclude.Include.NON_NULL)
        data class CurrentAchieveItem (
            var name: String = "",
            @JsonProperty("paper_score") var paperScore: Double? = 0.0,
            var mark: Double = 0.0,
            var retake: Double? = 0.0,
            var rebuild: Double? = 0.0,
            var credit: Double = 0.0,
        )

        class FailedAchieve: ArrayList<FailedAchieveItem>()

        data class FailedAchieveItem (
            var name: String = "",
            var mark: Double = 0.0
        )
    }
}