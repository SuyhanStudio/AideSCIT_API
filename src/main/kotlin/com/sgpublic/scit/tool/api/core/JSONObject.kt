package com.sgpublic.scit.tool.api.core

import org.json.JSONObject

/** 修复 */
class JSONObject(source: String) : JSONObject(source) {
    override fun getString(key: String): String {
        if (!isNull(key)){
            return super.getString(key)
        }
        return NULL as String
    }

    override fun get(key: String?): Any {
        if (!isNull(key)){
            return super.get(key)
        }
        return NULL
    }

    override fun getBoolean(key: String?): Boolean {
        if (!isNull(key)){
            return super.getBoolean(key)
        }
        return NULL as Boolean
    }

    override fun getLong(key: String?): Long {
        if (!isNull(key)){
            return super.getLong(key)
        }
        return NULL as Long
    }
}