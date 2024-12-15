package com.oyetech.repository.wallpaperImp.wallpaper.search

import com.oyetech.domain.repository.wallpaperApp.WallpaperSearchOperationRepository
import com.oyetech.models.wallpaperModels.responses.WallpaperSearchResponseData
import com.oyetech.remote.helper.interceptTrueForm
import com.oyetech.remote.wallpaperRemote.services.WallpaperServiceApi
import kotlinx.coroutines.flow.Flow

class WallpaperSearchOperationRepositoryImp(private var service: WallpaperServiceApi) :
    WallpaperSearchOperationRepository {

    override suspend fun getWallpapersWithSearchParameters(searchData: Map<String, Any>): Flow<WallpaperSearchResponseData> {
        return interceptTrueForm {

            service.getWallpapersWithSearchParameters(searchData)
        }
    }
}