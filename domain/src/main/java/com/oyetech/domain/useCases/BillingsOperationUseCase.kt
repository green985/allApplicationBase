package com.oyetech.domain.useCases

import com.oyetech.domain.repository.BillingsOperationRepository
import com.oyetech.models.entity.googleBilling.GoogleProductDetailResponseData
import com.oyetech.models.postBody.billings.GooglePlaySubscriptionInfoResponseData
import com.oyetech.models.postBody.billings.GoogleSubsPurchaseInfoRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-10.10.2022-
-14:43-
 **/

class BillingsOperationUseCase(private var repository: BillingsOperationRepository) {

    fun sendPurchaseAcknowledge(
        params: GoogleProductDetailResponseData,
    ): Flow<Boolean> {
        return repository.sendPurchaseAcknowledge(params)
    }

    fun getGooglePlaySubscriptionInfo(): Flow<GooglePlaySubscriptionInfoResponseData> {
        return repository.getGooglePlaySubscriptionInfo()
    }

    fun validateGooglePlayPurchase(
        params: GoogleSubsPurchaseInfoRequestBody,
    ): Flow<Boolean> {
        return repository.validateGooglePlayPurchase(params)
    }
}
