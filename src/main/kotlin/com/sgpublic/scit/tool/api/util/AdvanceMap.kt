package com.sgpublic.scit.tool.api.util

/**
 * LinkedHashMap 的扩展封装
 */
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
}