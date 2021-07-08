package com.sgpublic.aidescit.api.result

import com.sgpublic.aidescit.api.core.util.AdvanceMap

/**
 * 处理成功结果封装
 */
class SuccessResult(vararg pairs: Pair<String, Any>) : AdvanceMap() {
    init {
        put("code", 200)
        put("message", "success.")
        putAll(*pairs)
    }
}