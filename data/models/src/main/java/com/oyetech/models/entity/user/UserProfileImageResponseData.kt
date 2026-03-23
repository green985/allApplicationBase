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
    @param:Json(name = "id")
    var id: Long = 0,
    @param:Json(name = "isMainImage")
    var isMainImage: Boolean = false,
    @param:Json(name = "itemOrder")
    var itemOrder: Int = 0,
    @param:Json(name = "largeImageUrl")
    var largeImageUrl: String = "",
    @param:Json(name = "smallImageUrl")
    var smallImageUrl: String = "",
    @param:Json(name = "smallImageUrl")

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
