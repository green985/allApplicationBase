package com.oyetech.domain.repository.helpers

import android.app.Activity
import com.oyetech.models.entity.googleBilling.GoogleProductDetailResponseData
import com.oyetech.models.entity.messages.MessagesLimitInfoResponseData
import com.oyetech.models.postBody.billings.GoogleSubsPurchaseInfo
import com.oyetech.models.utils.states.SubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-6.10.2022-
-22:22-
 **/

interface GoogleSubscriptionOperationRepository {
    fun openSubscriptionsSellPage(
        selectedGoogleProductDetailResponseData:
        GoogleProductDetailResponseData,
        activity: Activity
    )

    val subscriptionStatusChannelFlow: MutableStateFlow<SubscriptionState>
    val getPremiumStatusAsyncChannelFlow: MutableStateFlow<GoogleSubsPurchaseInfo?>

    fun getPremiumStatusAsync()

    fun startBillingClient(
        repository: GoogleSubscriptionUIOperationRepository?,
        controlJustHistory: Boolean
    )
}

interface GoogleSubscriptionUIOperationRepository {
    fun onProductListShown()
    fun onProductListShownWithAppUsageCount()
    fun onProductDetailsInfo(productDetailsList: List<GoogleProductDetailResponseData>)
    fun onBillingResultInfoError(message: String? = "")
    fun onBillingResultInfo(responseCode: SubscriptionState)
    fun onPurchaseAcknowledgePurchase()
    fun onProductListShownWithFlag(
        fromMessageLimit: Boolean,
        messageLimitInfoResponseData: MessagesLimitInfoResponseData
    )
}
