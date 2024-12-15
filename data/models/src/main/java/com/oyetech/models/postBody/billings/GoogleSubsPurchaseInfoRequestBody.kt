package com.oyetech.models.postBody.billings

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-10.10.2022-
-14:36-
 **/

@JsonClass(generateAdapter = true)
@Parcelize
data class GoogleSubsPurchaseInfoRequestBody(
    @Json(name = "purchasePayload")
    var purchasePayload: String?,

    ) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class GoogleSubsPurchaseInfo(
    @Json(name = "acknowledged")
    var acknowledged: Boolean = false,
    @Json(name = "autoRenewing")
    var autoRenewing: Boolean = false,
    @Json(name = "orderId")
    var orderId: String = "",
    @Json(name = "developerPayload")
    var developerPayload: String = "",
    @Json(name = "packageName")
    var packageName: String = "",
    @Json(name = "productId")
    var productId: String = "",
    @Json(name = "purchaseState")
    var purchaseState: Int = 0,
    @Json(name = "purchaseTime")
    var purchaseTime: Long = 0,
    @Json(name = "purchaseToken")
    var purchaseToken: String = "",
    @Json(name = "quantity")
    var quantity: Int = 0
) : Parcelable

/*


 */
@JsonClass(generateAdapter = true)
@Parcelize
data class GooglePlaySubscriptionInfoResponseData(
    @Json(name = "expiryTime")
    var expiryTime: String = "",
    @Json(name = "isAcknowledged")
    var isAcknowledged: Boolean = false,
    @Json(name = "isActive")
    var isActive: Boolean = false,
    @Json(name = "isAutoRenewing")
    var isAutoRenewing: Boolean = false,
    @Json(name = "latestOrderId")
    var latestOrderId: String = "",
    @Json(name = "orderId")
    var orderId: String = "",
    @Json(name = "period")
    var period: String = "",
    @Json(name = "startTime")
    var startTime: String = ""
) : Parcelable
