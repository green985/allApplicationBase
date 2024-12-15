package com.oyetech.models.entity.googleBilling

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-6.10.2022-
-22:24-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class GoogleProductDetailResponseData(
    // val productId: String,
    // val productType: String,
    // val name: String,
    // val title: String,
    // val description: String,
    var offerToken: String,
    val billingPeriod: String,
    val billingCycleCount: Int,
    val formattedPrice: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val recurrenceMode: Int,
) : Parcelable
