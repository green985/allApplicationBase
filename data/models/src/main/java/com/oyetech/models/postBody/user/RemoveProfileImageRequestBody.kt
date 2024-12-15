package com.oyetech.models.postBody.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-30.05.2022-
-17:12-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class RemoveProfileImageRequestBody(
    @Json(name = "profileImageId") var profileImageId: Long = 0,
) : Parcelable
