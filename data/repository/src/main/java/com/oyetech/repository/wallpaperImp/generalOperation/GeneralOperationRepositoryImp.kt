package com.oyetech.repository.wallpaperImp.generalOperation

import com.oyetech.domain.repository.GeneralOperationRepository
import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import com.oyetech.remote.helper.interceptGenericResponseTrueForm
import com.oyetech.remote.wallpaperRemote.dataSource.GeneralOperationDataSource
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-26.12.2023-
-15:36-
 **/

class GeneralOperationRepositoryImp(private var generalOperationDataSource: GeneralOperationDataSource) :
    GeneralOperationRepository {

    override fun sendFeedback(feedbackOperationRequestBody: FeedbackOperationRequestBody): Flow<Boolean> {
        return interceptGenericResponseTrueForm {
            generalOperationDataSource.sendFeedback(feedbackOperationRequestBody)
        }
    }

}