package com.oyetech.domain.useCases

import com.oyetech.domain.repository.FeedRepository
import com.oyetech.models.entity.feed.FeedDataResponse
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-4.03.2022-
-00:19-
 **/

class FeedOperationUseCase(private var feedRepository: FeedRepository) {

    suspend fun getUserFeedList(): Flow<List<FeedDataResponse>> {

        return feedRepository.getUserFeed()
    }
}
