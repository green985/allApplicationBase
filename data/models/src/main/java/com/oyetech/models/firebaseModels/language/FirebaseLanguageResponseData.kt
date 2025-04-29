package com.oyetech.models.firebaseModels.language

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.utils.moshi.deserialize
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseLanguageResponseDataWrapper(
    var wrapper: List<FirebaseLanguageResponseData> = emptyList(),
    val saltValue: List<String> = emptyList(),
) : Parcelable {
    companion object {
        fun firebaseDocumentToObject(wrapper: HashMap<*, *>): List<FirebaseLanguageResponseData> {
            val list = mutableListOf<FirebaseLanguageResponseData>()
            wrapper.forEach { (key, value) ->
                (value as? List<*>)?.forEach {
                    try {
                        val wrapperValue =
                            (it as String).deserialize<FirebaseLanguageResponseDataWrapper>()
                        list.addAll(wrapperValue?.wrapper ?: emptyList())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Timber.e("Error: ${e.message}")
                    }
                }
            }
            return list
        }
    }
}

fun FirebaseLanguageResponseDataWrapper.toResponseData(): List<FirebaseLanguageResponseData> {
    // it key ile bunlari gizleyip acabilirsin
    // ornegin x uygulamasina ozel y ye ozel vs vs
    // ama uygulama icinde hepsini al ne fark eder
    val list = mutableListOf<FirebaseLanguageResponseData>()

    return wrapper
}

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class FirebaseLanguageResponseData(
    var key: String = "",
    var value: String = "",
) : Parcelable

/**
 * {
 *   "wrapper": [
 *     {
 *       "key": "errorText",
 *       "value": "Error"
 *     }
 *   ]
 * }
 */