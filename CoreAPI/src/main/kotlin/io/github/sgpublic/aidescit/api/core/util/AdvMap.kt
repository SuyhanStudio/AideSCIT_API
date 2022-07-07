package io.github.sgpublic.aidescit.api.core.util

import com.fasterxml.jackson.annotation.JsonInclude
import io.github.sgpublic.aidescit.api.exceptions.ServerRuntimeException
import java.lang.StringBuilder

/**
 * LinkedHashMap 的扩展封装
 */
@Suppress("RedundantModalityModifier")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class AdvMap(vararg pairs: Pair<String, Any?>) : LinkedHashMap<String, Any?>() {
    init {
        putAll(*pairs)
    }

    /**
     * 添加键值对到 Map
     * @param pair 键值对
     */
    final fun put(pair: Pair<String, Any?>): AdvMap {
        put(pair.first, pair.second)
        return this
    }

    /**
     * 添加多个键值对到 Map
     * @param pairs 多个键值对
     */
    final fun putAll(vararg pairs: Pair<String, Any?>): AdvMap {
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

    override fun toString(): String {
        return readToString(this)
    }

    fun toSortedString(): String {
        return readToString(toSortedMap())
    }

    companion object {
        fun <K, V> readToString(map: Map<K, V?>): String {
            val builder = StringBuilder("{")
            map.forEach { (t, u) ->
                builder.append("\"$t\":")
                when (u) {
                    is String -> {
                        builder.append("\"$u\"")
                    }
                    null -> {
                        builder.append("null")
                    }
                    else -> {
                        builder.append(u)
                    }
                }
                builder.append(",")
            }
            return builder.substring(0, builder.length - 1) + "}"
        }
    }
}

public fun advMapOf(vararg pairs: Pair<String, Any?>): AdvMap {
    return AdvMap(*pairs)
}

public fun LinkedHashMap<String, Any?>.toString(): String {
    return AdvMap.readToString(this)
}