package com.oyetech.reviewer

import com.oyetech.domain.repository.helpers.AppReviewControllerRepository
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.models.utils.const.HelperConstant
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-6.10.2022-
-15:42-
 **/

/**
 *
 * uygulamanin en az 4. acilisinda olsun
kullaniciya toplamda 10 mesaj gelmis olsun
 */
class ReviewOperationController(
    private var sharedPrefKey: SharedHelperRepository,
    // private var messageOperationUseCase: MessagesOperationUseCase,
) : AppReviewControllerRepository {

    private var reviewCanShowStateFlow = MutableStateFlow<Boolean>(false)

    override fun controlReviewCanShow() {
        if (sharedPrefKey.getReviewUserDontWantSee()) {
            return
        }

        if (sharedPrefKey.isReviewAlreadyShown()) {
            return
        }

        val totalAppOpenCount = sharedPrefKey.getTotalAppOpenCount()
        if (totalAppOpenCount < HelperConstant.APP_FIRST_OPEN_COUNT_THRESHOLD) {
            Timber.d("first open thressholl " + totalAppOpenCount)
            return
        }

        reviewCanShowStateFlow.value = true
    }

    override fun getReviewCanShowState(): MutableStateFlow<Boolean> {
        return reviewCanShowStateFlow
    }
}
