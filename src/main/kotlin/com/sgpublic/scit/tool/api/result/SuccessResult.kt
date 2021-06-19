package com.sgpublic.scit.tool.api.result

import com.sgpublic.scit.tool.api.core.SimpleMap

class SuccessResult(vararg pairs: Pair<String, Any>) : SimpleMap() {
    init {
        put("code", 200)
        put("message", "success.")
        putAll(pairs)
    }
}