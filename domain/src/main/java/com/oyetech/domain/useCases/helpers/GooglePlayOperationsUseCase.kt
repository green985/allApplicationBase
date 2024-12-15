package com.oyetech.domain.useCases.helpers

import android.app.Activity
import com.oyetech.domain.repository.helpers.GoogleSubscriptionOperationRepository
import com.oyetech.domain.repository.helpers.GoogleSubscriptionUIOperationRepository
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.models.entity.googleBilling.GoogleProductDetailResponseData
import com.oyetech.models.entity.messages.MessagesLimitInfoResponseData
import com.oyetech.models.postBody.billings.GoogleSubsPurchaseInfo
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.states.SubscriptionState
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-6.10.2022-
-21:45-
 **/

class GooglePlayOperationsUseCase(private var repository: GoogleSubscriptionOperationRepository) {

    var uiRepository: GoogleSubscriptionUIOperationRepository? = null

    var isAppUsageSubscriptionDialogAlreadyShown = false

    val sharedPrefUseCase: SharedOperationUseCase by KoinJavaComponent.inject(SharedOperationUseCase::class.java)

    fun checkGooglePlayServicesAvailable(): Boolean {
        // todo will be fixed.
        // todo will be fixed.

        return true
    }

    fun onProductListShown() {
        uiRepository?.onProductListShown()
    }

    fun onProductListShownWithAppUsageCount() {
        uiRepository?.onProductListShownWithAppUsageCount()
        sharedPrefUseCase.putDateWhenSubsDialogShow()
    }

    fun onProductListShownWithFlag(
        isFromMessageLimit: Boolean,
        messageLimitInfoResponseData: MessagesLimitInfoResponseData
    ) {
        uiRepository?.onProductListShownWithFlag(isFromMessageLimit, messageLimitInfoResponseData)
    }

    fun getPremiumStatusAsync() {
        repository.getPremiumStatusAsync()
    }

    fun getPremiumStatusForUser(): Flow<GoogleSubsPurchaseInfo?> {
        return repository.getPremiumStatusAsyncChannelFlow
    }

    fun isPremiumUser(): Boolean {
        return repository.subscriptionStatusChannelFlow.value == SubscriptionState.ACTIVE
    }

    fun getSubscriptionStatusFlow(): Flow<SubscriptionState> {
        return repository.subscriptionStatusChannelFlow
    }

    fun initAndStartBillingOperation(uiRepository: GoogleSubscriptionUIOperationRepository) {
        repository.startBillingClient(uiRepository, controlJustHistory = false)
    }

    fun controlPurchases() {
        repository.startBillingClient(null, controlJustHistory = false)
    }

    fun controlPurchaseStatus() {
        repository.startBillingClient(null, controlJustHistory = true)
    }

    fun tryToShowBillingScreen() {
    }

    fun openSubscriptionsSellPage(
        selectedGoogleProductDetailResponseData: GoogleProductDetailResponseData,
        activity: Activity
    ) {
        repository.openSubscriptionsSellPage(
            selectedGoogleProductDetailResponseData = selectedGoogleProductDetailResponseData,
            activity
        )
    }

    fun showSubscriptionSellPageForOverAppUsage() {
        if (isPremiumUser()) {
            Timber.d("totalAppCopunt is premium userr.....")
            return
        } else {
            Timber.d("totalAppCopunt is not premium userr.....")
        }

        var totalAppOpenCount = sharedPrefUseCase.getTotalAppOpenCount()

        Timber.d("totalAppCopunt === " + totalAppOpenCount)

        if (!isAppUsageSubscriptionDialogAlreadyShown) {
            Timber.d("isAppUsageSubscriptionDialogCanShow == " + isAppUsageSubscriptionDialogAlreadyShown)
            return
        }

        if (totalAppOpenCount > HelperConstant.APP_SUBS_DIALOG_SHOW_THRESHOLD
        ) {

            if (!sharedPrefUseCase.getIsDateWhenSubsDialogShow()) {

                onProductListShownWithAppUsageCount()
            } else {
                var isSubsDialogCanShow = sharedPrefUseCase.isSubsDialogCanShow()
                Timber.d("totalAppCopunt === " + isSubsDialogCanShow)

                if (isSubsDialogCanShow) {
                    onProductListShownWithAppUsageCount()
                } else {
                    Timber.d("totalAppCopunt not yettttt")
                }
            }
        } else {
            Timber.d("totalAppCopunt count not trigger")
        }
    }
}
