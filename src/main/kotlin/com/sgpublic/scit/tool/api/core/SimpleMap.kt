package com.sgpublic.scit.tool.api.core

import java.util.AbstractMap

open class SimpleMap: MutableMap<String, Any> {
    private var sizeValue = 0

    private val keyCollection: ArrayList<String> = ArrayList()
    private val valueCollection: ArrayList<Any> = ArrayList()

    override val size: Int get() = sizeValue

    final override fun containsKey(key: String): Boolean {
        return keyCollection.contains(key)
    }

    final override fun containsValue(value: Any): Boolean {
        return valueCollection.contains(value)
    }

    final override fun get(key: String): Any? {
        if (!containsKey(key)){
            return null
        }
        val index = keyCollection.indexOf(key)
        return valueCollection[index]
    }

    final override fun isEmpty(): Boolean {
        return size == 0
    }

    final override val entries: MutableSet<MutableMap.MutableEntry<String, Any>> get() {
        val set = mutableSetOf<MutableMap.MutableEntry<String, Any>>()
        for (index in 0 until size){
            set.add(AbstractMap.SimpleEntry(keyCollection[index], valueCollection[index]))
        }
        return set
    }

    final override val keys: MutableSet<String> get() {
        val set = mutableSetOf<String>()
        for (index in 0 until size){
            set.add(keyCollection[index])
        }
        return set
    }

    final override val values: MutableCollection<Any> get() {
        val set = mutableListOf<Any>()
        for (index in 0 until size){
            set.add(valueCollection[index])
        }
        return set
    }

    final override fun clear() {
        keyCollection.clear()
        valueCollection.clear()
        sizeValue = 0
    }

    final override fun put(key: String, value: Any): Any {
        val index = if (containsKey(key)){
            val index = keyCollection.indexOf(key)
            valueCollection[index] = value
            index
        } else {
            keyCollection.add(key)
            valueCollection.add(value)
            valueCollection.size
        }
        return valueCollection[index]
    }

    final fun put(pair: Pair<String, Any>){
        put(pair.first, pair.second)
    }

    final fun putAll(vararg pairs: Pair<String, Any>){
        for (pair in pairs){
            put(pair.first, pair.second)
        }
    }

    final override fun putAll(from: Map<out String, Any>) {
        from.forEach {
            put(it.key, it.value)
        }
    }

    final override fun remove(key: String): Any? {
        val value = if (containsKey(key)){
            val index = keyCollection.indexOf(key)
            val value = valueCollection[index]
            keyCollection.removeAt(index)
            valueCollection.removeAt(index)
            value
        } else {
            null
        }
        sizeValue--
        return value
    }
}