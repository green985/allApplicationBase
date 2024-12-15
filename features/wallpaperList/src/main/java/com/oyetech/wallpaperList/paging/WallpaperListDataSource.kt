package com.oyetech.wallpaperList.paging

import com.oyetech.domain.useCases.wallpaperApp.WallpaperSearchOperationUseCase
import com.oyetech.materialViews.old.paging.BasePagingDataSource
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-8.02.2024-
-16:10-
 **/

class WallpaperListDataSource(
    private val scope: CoroutineScope,
    private var wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase,
) : BasePagingDataSource<WallpaperResponseData>() {

    var pageIndex = 0

    override fun loadAfter(
        params: androidx.paging.PageKeyedDataSource.LoadParams<Int>,
        callback: androidx.paging.PageKeyedDataSource.LoadCallback<Int, WallpaperResponseData>,
    ) {
        Timber.d("data source  loadAfter===")

        if (endOfList) return

        setLoadingStatus(isSuccess = false)


        scope.launch(Dispatchers.IO) {
            try {
                val responseData =
                    wallpaperSearchOperationUseCase.getWallpaperListWithSearchParameters(params.key)
                        .first()
                checkthread()
                setRetry(null)
                setLoadingStatus(isSuccess = true)
                responseData.wallpaperData.let {
                    Timber.d("data source ===" + it.size)

                    callback.onResult(it, responseData.meta.current_page + 1)
                }
            } catch (e: Exception) {
                setLoadingStatus(isSuccess = false, errorThrowable = e)
                setRetry {
                    loadAfter(params, callback)
                }
                Timber.d("data source ===" + e.message)
            }
        }

    }

    private fun checkthread() {

        Timber.d("data source ===" + Thread.currentThread().name)
    }

    override fun loadBefore(
        params: androidx.paging.PageKeyedDataSource.LoadParams<Int>,
        callback: androidx.paging.PageKeyedDataSource.LoadCallback<Int, WallpaperResponseData>,
    ) {
        //TODO
    }

    override fun loadInitial(
        params: androidx.paging.PageKeyedDataSource.LoadInitialParams<Int>,
        callback: androidx.paging.PageKeyedDataSource.LoadInitialCallback<Int, WallpaperResponseData>,
    ) {
        setLoadingStatus(isSuccess = false, isFirstInit = true)
        scope.launch(Dispatchers.IO) {
            try {
                var responseData =
                    wallpaperSearchOperationUseCase.getWallpaperListWithSearchParameters().first()



                responseData.wallpaperData.let {
                    setLoadingStatus(
                        isSuccess = true,
                        isFirstInit = true,
                        list = it
                    )
                    Timber.d("data source ===" + it.size)
                    callback.onResult(it, null, responseData.meta.current_page + 1)
                    setRetry(null)
                }
            } catch (e: Exception) {
                Timber.d("data source ===" + e.message)
                setLoadingStatus(isSuccess = false, isFirstInit = true, errorThrowable = e)
                setRetry {
                    loadInitial(params, callback)
                }

            }
        }


    }


}

