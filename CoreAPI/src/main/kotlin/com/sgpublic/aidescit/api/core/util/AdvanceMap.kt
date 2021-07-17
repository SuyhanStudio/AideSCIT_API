package com.sgpublic.aidescit.api.core.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.sgpublic.aidescit.api.exceptions.ServerRuntimeException

/**
 * LinkedHashMap 的扩展封装
 */
@Suppress("RedundantModalityModifier")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class AdvanceMap(vararg pairs: Pair<String, Any>) : LinkedHashMap<String, Any>() {
    init {
        putAll(*pairs)
    }

    /**
     * 添加键值对到 Map
     * @param pair 键值对
     */
    final fun put(pair: Pair<String, Any>): AdvanceMap {
        put(pair.first, pair.second)
        return this
    }

    /**
     * 添加多个键值对到 Map
     * @param pairs 多个键值对
     */
    final fun putAll(vararg pairs: Pair<String, Any>): AdvanceMap {
        for (pair in pairs){
            put(pair.first, pair.second)
        }
        return this
    }

    final inline fun <reified T> getSet(name: String, obj: T): T {
        if (!containsKey(name)){
            put(name, obj as Any)
        }
        get(name).run {
            if (this !is T){
                throw ServerRuntimeException.INTERNAL_ERROR
            }
            return this
        }
    }
}