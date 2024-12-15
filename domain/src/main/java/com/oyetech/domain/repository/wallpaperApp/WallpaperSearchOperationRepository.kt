package com.oyetech.domain.repository.wallpaperApp

import com.oyetech.models.wallpaperModels.responses.WallpaperSearchResponseData
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-7.02.2024-
-15:23-
 **/

interface WallpaperSearchOperationRepository {
    suspend fun getWallpapersWithSearchParameters(searchData: Map<String, Any>): Flow<WallpaperSearchResponseData>
}