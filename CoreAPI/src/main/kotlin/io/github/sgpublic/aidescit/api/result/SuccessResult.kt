package io.github.sgpublic.aidescit.api.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.sgpublic.aidescit.api.core.util.AdvMap

/**
 * 处理成功结果封装
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class SuccessResult(vararg pairs: Pair<String, Any>) : AdvMap() {
    init {
        put("code", 200)
        put("message", "success.")
        putAll(*pairs)
    }
}