package com.oyetech.domain.repository

import com.oyetech.models.entity.feed.FeedDataResponse
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-4.03.2022-
-00:15-
 **/

interface FeedRepository {

    suspend fun getUserFeed(): Flow<List<FeedDataResponse>>
}
