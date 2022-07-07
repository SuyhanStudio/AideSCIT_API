package io.github.sgpublic.aidescit.api.core.util

/**
 * 启动传参读取
 */
class ArgumentReader(args: Array<String>) {
    private val singleItems: ArrayList<String> = arrayListOf()
    private val strings: MutableMap<String, String> = mutableMapOf()
    private val ints: MutableMap<String, Int> = mutableMapOf()
    private val doubles: MutableMap<String, Double> = mutableMapOf()
    private val booleans: MutableMap<String, Boolean> = mutableMapOf()

    companion object {
        /**
         * 整理请求表单，忽略多余参数
         * @param map 请求传入的表单
         * @return 返回按 key 排序的键值对集合
         */
        fun readRequestMap(map: Map<String, Array<String>>): Map<String, String> {
            val result = mutableMapOf<String, String>()
            for ((key, values) in map.toSortedMap()) {
                if (values.isNullOrEmpty()){
                    continue
                }
                result[key] = values[0]
            }
            return result
        }
    }

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

    /**
     * 判断单个参数是否存在
     * @param key 参数，例如“--debug”
     */
    fun containsItem(key: String): Boolean {
        return singleItems.contains(key)
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @return 返回参数值，若参数不存在则返回 null
     */
    fun getString(key: String): String? {
        return getString(key, null)
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @param default 默认值
     * @return 返回参数值，若参数不存在则返回默认值
     */
    fun getString(key: String, default: String?): String? {
        if (!strings.containsKey(key)){
            return default
        }
        return strings[key]
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @return 返回参数值，若参数不存在则返回 null
     */
    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @param default 默认值
     * @return 返回参数值，若参数不存在则返回默认值
     */
    fun getInt(key: String, default: Int): Int {
        ints[key]?.let {
            return it
        }
        return default
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @return 返回参数值，若参数不存在则返回 null
     */
    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    /**
     * 获取 Boolean 类型的参数值
     * @param key 参数名称
     * @param default 默认值
     * @return 返回参数值，若参数不存在则返回默认值
     */
    fun getBoolean(key: String, default: Boolean): Boolean {
        booleans[key]?.let {
            return it
        }
        return default
    }

    /**
     * 获取 String 类型的参数值
     * @param key 参数名称
     * @return 返回参数值，若参数不存在则返回 null
     */
    fun getDouble(key: String): Double {
        return getDouble(key, 0.0)
    }

    /**
     * 获取 Double 类型的参数值
     * @param key 参数名称
     * @param default 默认值
     * @return 返回参数值，若参数不存在则返回默认值
     */
    fun getDouble(key: String, default: Double): Double {
        doubles[key]?.let {
            return it
        }
        return default
    }
}