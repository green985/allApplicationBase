package com.oyetech.remote.wallpaperRemote.dataSource

import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import com.oyetech.remote.wallpaperRemote.services.BibleService

/**
Created by Erdi Özbek
-26.12.2023-
-15:33-
 **/

class GeneralOperationDataSource(private var service: BibleService) {

    suspend fun sendFeedback(feedbackOperationRequestBody: FeedbackOperationRequestBody) =
        service.sendFeedback(feedbackOperationRequestBody)


}