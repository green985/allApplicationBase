package com.oyetech.domain.repository

import com.oyetech.models.entity.googleBilling.GoogleProductDetailResponseData
import com.oyetech.models.postBody.billings.GooglePlaySubscriptionInfoResponseData
import com.oyetech.models.postBody.billings.GoogleSubsPurchaseInfoRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-10.10.2022-
-14:41-
 **/

interface BillingsOperationRepository {
    fun sendPurchaseAcknowledge(
        params: GoogleProductDetailResponseData,
    ): Flow<Boolean>

    fun validateGooglePlayPurchase(params: GoogleSubsPurchaseInfoRequestBody): Flow<Boolean>
    fun getGooglePlaySubscriptionInfo(): Flow<GooglePlaySubscriptionInfoResponseData>
}
