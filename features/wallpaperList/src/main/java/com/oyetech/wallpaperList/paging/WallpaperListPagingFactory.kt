package com.oyetech.wallpaperList.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.oyetech.domain.useCases.wallpaperApp.WallpaperSearchOperationUseCase
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import kotlinx.coroutines.CoroutineScope

class WallpaperListPagingFactory
constructor(
    private val scope: CoroutineScope,
    private var wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase,
) : DataSource.Factory<Int, WallpaperResponseData>() {

    companion object {

        val pagedListConfig: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(100)
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(1)
            .setPrefetchDistance(1)
            .build()

    }

    var liveDataSource = MutableLiveData<WallpaperListDataSource>()

    var pageIndex = 1
    override fun create(): DataSource<Int, WallpaperResponseData> {
        var dataSource = WallpaperListDataSource(scope, wallpaperSearchOperationUseCase)
        dataSource.pageIndex = pageIndex
        liveDataSource.postValue(dataSource)
        return dataSource
    }


}

