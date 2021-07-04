package com.sgpublic.scit.tool.api.core

import org.json.JSONObject

/** 修复获取键值为 null 的情况下报错的问题 */
class NullableJSONObject(source: String) : JSONObject(source) {
    fun getString(key: String, default: String): String {
        if (!isNull(key)){
            return super.getString(key)
        }
        return default
    }

    fun get(key: String, default: Any): Any {
        if (!isNull(key)){
            return super.get(key)
        }
        return default
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        if (!isNull(key)){
            return super.getBoolean(key)
        }
        return default
    }

    fun getLong(key: String?, default: Long = 0): Long {
        if (!isNull(key)){
            return super.getLong(key)
        }
        return default
    }
}