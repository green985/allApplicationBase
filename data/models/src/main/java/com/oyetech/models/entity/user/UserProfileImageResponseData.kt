package com.oyetech.models.entity.user

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-30.05.2022-
-16:29-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserProfileImageResponseData(
    @Json(name = "id")
    var id: Long = 0,
    @Json(name = "isMainImage")
    var isMainImage: Boolean = false,
    @Json(name = "itemOrder")
    var itemOrder: Int = 0,
    @Json(name = "largeImageUrl")
    var largeImageUrl: String = "",
    @Json(name = "smallImageUrl")
    var smallImageUrl: String = "",
    @Json(name = "smallImageUrl")

    @Transient
    var isLoading: Boolean = false,
) : Parcelable {

    fun clearForRemove() {
        this.largeImageUrl = ""
        this.isMainImage = false
        this.isLoading = false
        this.smallImageUrl = ""
        this.itemOrder = 0
    }
}
