package com.sgpublic.aidescit.api.core

class Cookies private constructor(private val cookies: Map<String, ArrayList<Any>>){
    override fun toString(): String {
        return StringBuilder().run {
            for ((key, values) in cookies){
                for (value in values) {
                    append("$key=$value; ")
                }
            }
        }.toString()
    }

    class Builder() {
        private val cookies: MutableMap<String, ArrayList<Any>> = mutableMapOf()

        fun add(key: String, value: Any){
            if (cookies[key] == null){
                cookies[key] = ArrayList()
            }
            cookies[key]?.add(value)
        }

        fun build() = Cookies(cookies)
    }
}