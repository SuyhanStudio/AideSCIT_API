package io.github.sgpublic.aidescit.api.core.util

import com.google.gson.Gson
import okhttp3.Response
import java.io.Serializable
import javax.persistence.Transient

@Transient
fun Serializable?.toGson(): String {
    return Gson().toJson(this)
}

fun <T> Response.jsonBody(clazz: Class<T>): T {
    return Gson().fromJson(this.body?.string().toString(), clazz)
}