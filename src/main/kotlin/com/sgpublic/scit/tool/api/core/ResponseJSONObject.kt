package com.sgpublic.scit.tool.api.core

import org.json.JSONObject

open class ResponseJSONObject : JSONObject() {
    final override fun put(key: String?, value: Int): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: Any?): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: Boolean): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: Double): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: Float): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: Long): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: MutableCollection<*>?): JSONObject {
        return super.put(key, value)
    }

    final override fun put(key: String?, value: MutableMap<*, *>?): JSONObject {
        return super.put(key, value)
    }
}