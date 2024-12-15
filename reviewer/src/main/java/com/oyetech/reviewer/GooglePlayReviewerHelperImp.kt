package com.oyetech.reviewer

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.helpers.AppReviewRepository
import com.oyetech.models.enums.ReviewStatus
import com.oyetech.models.enums.ReviewStatus.Error
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-5.10.2022-
-17:20-
 **/

class GooglePlayReviewerHelperImp(
    private var context: Context,
    private val activityProviderUseCase: ActivityProviderUseCase,
) : AppReviewRepository {

    lateinit var appReviewManager: ReviewManager
    lateinit var fakeAppReviewManager: FakeReviewManager

    val reviewOperationStatusFlow = MutableStateFlow(ReviewStatus.Idle)

    override fun startAppReviewOperation() {
        appReviewManager = ReviewManagerFactory.create(context)

        val request = appReviewManager.requestReviewFlow()
        reviewOperationStatusFlow.value = ReviewStatus.Start
        request.addOnCompleteListener(getOnCompleteListener())
    }

    override fun fakeStartAppReviewOperation() {
        fakeAppReviewManager = FakeReviewManager(context)

        val request = fakeAppReviewManager.requestReviewFlow()
        reviewOperationStatusFlow.value = ReviewStatus.Start
        request.addOnCompleteListener(getOnCompleteListener(true))
    }

    override fun getReviewStatusState(): MutableStateFlow<ReviewStatus> {
        return reviewOperationStatusFlow
    }

    private fun getOnCompleteListener(
        isFake: Boolean = false,
    ): OnCompleteListener<ReviewInfo> {
        return OnCompleteListener<ReviewInfo> { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                Timber.d("reviewInffooooo ?= " + reviewInfo.toString())
                if (isFake) {
                    launchFakeAppReviewOperation(reviewInfo)
                } else {
                    launchAppReviewOperation(reviewInfo)
                }
            } else {
                reviewOperationStatusFlow.value = Error
                // There was some problem, log or handle the error code.
            }
        }
    }

    private fun launchAppReviewOperation(
        reviewInfo: ReviewInfo?,
    ) {
        if (reviewInfo == null) {
            reviewOperationStatusFlow.value = ReviewStatus.Error
            Timber.d("reviewInfoNulll")
            return
        }
        if (activityProviderUseCase.getCurrentActivity() == null) return
        val flow = appReviewManager.launchReviewFlow(
            activityProviderUseCase.getCurrentActivity()!!,
            reviewInfo
        )
        flow.addOnCompleteListener { _ ->
            reviewOperationStatusFlow.value = ReviewStatus.Completed
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }

    private fun launchFakeAppReviewOperation(
        reviewInfo: ReviewInfo?,
    ) {
        if (reviewInfo == null) {
            Timber.d("reviewInfoNulll")
            reviewOperationStatusFlow.value = ReviewStatus.Error
            return
        }
        if (activityProviderUseCase.getCurrentActivity() == null) return
        val flow =
            fakeAppReviewManager.launchReviewFlow(
                activityProviderUseCase.getCurrentActivity()!!,
                reviewInfo
            )
        flow.addOnCompleteListener { _ ->
            reviewOperationStatusFlow.value = ReviewStatus.Completed
        }
    }
}

