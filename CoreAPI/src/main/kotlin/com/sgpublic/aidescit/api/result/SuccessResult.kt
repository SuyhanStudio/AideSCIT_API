package com.sgpublic.aidescit.api.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.sgpublic.aidescit.api.core.util.AdvanceMap

/**
 * 处理成功结果封装
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class SuccessResult(vararg pairs: Pair<String, Any>) : AdvanceMap() {
    init {
        put("code", 200)
        put("message", "success.")
        putAll(*pairs)
    }
}