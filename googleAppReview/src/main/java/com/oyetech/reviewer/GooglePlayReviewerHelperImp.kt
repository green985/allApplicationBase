package com.oyetech.reviewer

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.oyetech.domain.repository.helpers.AppReviewRepository
import com.oyetech.domain.repository.helpers.AppReviewResultRepository
import timber.log.Timber

/**
Created by Erdi Özbek
-5.10.2022-
-17:20-
 **/

class GooglePlayReviewerHelperImp(private var context: Context) : AppReviewRepository {

    lateinit var appReviewManager: ReviewManager
    lateinit var fakeAppReviewManager: FakeReviewManager

    override fun startAppReviewOperation(resultRepository: AppReviewResultRepository) {
        appReviewManager = ReviewManagerFactory.create(context)

        val request = appReviewManager.requestReviewFlow()
        resultRepository.onStartAppReviewOperation()
        request.addOnCompleteListener(getOnCompleteListener(resultRepository))
    }

    override fun fakeStartAppReviewOperation(resultRepository: AppReviewResultRepository) {
        fakeAppReviewManager = FakeReviewManager(context)

        val request = fakeAppReviewManager.requestReviewFlow()
        resultRepository.onStartAppReviewOperation()
        request.addOnCompleteListener(getOnCompleteListener(resultRepository, true))
    }

    private fun getOnCompleteListener(
        resultRepository: AppReviewResultRepository,
        isFake: Boolean = false,
    ): OnCompleteListener<ReviewInfo> {
        return OnCompleteListener<ReviewInfo> { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                Timber.d("reviewInffooooo ?= " + reviewInfo.toString())
                if (isFake) {
                    launchFakeAppReviewOperation(reviewInfo, resultRepository)
                } else {
                    launchAppReviewOperation(reviewInfo, resultRepository)
                }
            } else {
                // There was some problem, log or handle the error code.
                resultRepository.onErrorAppReviewOperation(task.exception)
            }
        }
    }

    private fun launchAppReviewOperation(
        reviewInfo: ReviewInfo?,
        resultRepository: AppReviewResultRepository,
    ) {
        if (reviewInfo == null) {
            Timber.d("reviewInfoNulll")
            resultRepository.onErrorAppReviewOperation(Exception("null"))
            return
        }
        if (resultRepository.getActivityy() == null) return
        val flow = appReviewManager.launchReviewFlow(resultRepository.getActivityy()!!, reviewInfo)
        flow.addOnCompleteListener { _ ->

            resultRepository.onCompleteAppReviewOperation()
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }

    private fun launchFakeAppReviewOperation(
        reviewInfo: ReviewInfo?,
        resultRepository: AppReviewResultRepository,
    ) {
        if (reviewInfo == null) {
            Timber.d("reviewInfoNulll")
            resultRepository.onErrorAppReviewOperation(Exception("null"))
            return
        }
        if (resultRepository.getActivityy() == null) return
        val flow =
            fakeAppReviewManager.launchReviewFlow(resultRepository.getActivityy()!!, reviewInfo)
        flow.addOnCompleteListener { _ ->
            resultRepository.onCompleteAppReviewOperation()
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }
}
