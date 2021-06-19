package com.sgpublic.scit.tool.api.core

class ArgumentReader(args: Array<String>) {
    private val singleItems: ArrayList<String> = arrayListOf()
    private val strings: MutableMap<String, String> = mutableMapOf()
    private val ints: MutableMap<String, Int> = mutableMapOf()
    private val doubles: MutableMap<String, Double> = mutableMapOf()
    private val booleans: MutableMap<String, Boolean> = mutableMapOf()

    /**
     * 载入所有启动参数
     */
    init {
        for (arg in args){
            if (!arg.contains("=")){
                singleItems.add(arg)
                continue
            }
            val pair = arg.split("=")
            val key = pair[0]
            val value = pair[1]
            var valueCast: Any?
            valueCast = value.toIntOrNull()
            if (valueCast != null){
                ints[key] = valueCast
                continue
            }
            valueCast = value.toBooleanStrictOrNull()
            if (valueCast != null){
                booleans[key] = valueCast
                continue
            }
            valueCast = value.toDoubleOrNull()
            if (valueCast != null){
                doubles[key] = valueCast
                continue
            }
            strings[key] = value
        }
    }

    fun containsItem(key: String): Boolean {
        return singleItems.contains(key)
    }

    fun getString(key: String): String? {
        return getString(key, null)
    }

    fun getString(key: String, default: String?): String? {
        if (!strings.containsKey(key)){
            return default
        }
        return strings[key]
    }

    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    fun getInt(key: String, default: Int): Int {
        ints[key]?.let {
            return it
        }
        return default
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        booleans[key]?.let {
            return it
        }
        return default
    }

    fun getDouble(key: String): Double {
        return getDouble(key, 0.0)
    }

    fun getDouble(key: String, default: Double): Double {
        doubles[key]?.let {
            return it
        }
        return default
    }
}