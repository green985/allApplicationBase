package com.oyetech.models.utils.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.source
import java.io.InputStream

object MoshiExtensions {
    var moshi: Moshi =
        Moshi.Builder().add(DefaultIfNullFactory()).addLast(KotlinJsonAdapterFactory()).build()

    fun init(moshi: Moshi) {
        this.moshi = moshi
    }
}

@Throws(JsonDataException::class)
inline fun <reified T> String.deserialize(): T? {
    try {
        val jsonAdapter = MoshiExtensions.moshi.adapter(T::class.java).lenient()
        return jsonAdapter.fromJson(this)
    } catch (e: Exception) {
        return null
    }
}

@Throws(JsonDataException::class)
inline fun <reified T> String.deserializeList(): List<T>? {
    val type = Types.newParameterizedType(MutableList::class.java, T::class.java)
    var jsonAdapter: JsonAdapter<List<T>> = MoshiExtensions.moshi.adapter(type)
    jsonAdapter = jsonAdapter.lenient()
    return jsonAdapter.fromJson(this)
}
// write function which can make hash map from json string

inline fun <reified T> String.deserializeHashMap(): Map<String, T>? {
    val type = Types.newParameterizedType(Map::class.java, String::class.java, T::class.java)
    var jsonAdapter: JsonAdapter<Map<String, T>> = MoshiExtensions.moshi.adapter(type)
    jsonAdapter = jsonAdapter.lenient()
    return jsonAdapter.fromJson(this)
}

inline fun <reified T> convertFromRawJsonFile(inputStream: InputStream): List<T>? {
    val type = Types.newParameterizedType(MutableList::class.java, T::class.java)
    val jsonAdapter: JsonAdapter<List<T>> = MoshiExtensions.moshi.adapter<List<T>?>(type).lenient()

    var value = jsonAdapter.fromJson(inputStream.source().buffer())

    return value
}

@Suppress("CheckResult")
fun String.canConvertTo(type: Class<*>): Boolean {
    return try {
        val jsonAdapter = MoshiExtensions.moshi.adapter(type).lenient()
        jsonAdapter.fromJson(this)
        true
    } catch (exception: Exception) {
        exception.printStackTrace()
        false
    }
}

inline fun <reified T> T.serialize(): String {
    val jsonAdapter = MoshiExtensions.moshi.adapter(T::class.java).lenient()
    return jsonAdapter.toJson(this)
}
