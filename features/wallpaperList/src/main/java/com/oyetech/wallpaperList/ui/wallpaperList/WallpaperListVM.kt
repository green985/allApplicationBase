package com.oyetech.wallpaperList.ui.wallpaperList;

import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.utils.SingleLiveEvent
import com.oyetech.domain.repository.viewHelperRepositories.ImageViewerOverlayRepository
import com.oyetech.domain.repository.viewHelperRepositories.ImageViewerRepository
import com.oyetech.domain.useCases.wallpaperApp.WallpaperSearchOperationUseCase
import com.oyetech.models.utils.states.ViewState
import com.oyetech.models.utils.states.ViewStatus
import com.oyetech.models.wallpaperModels.helperModels.image.ImageViewerPropertyData
import com.oyetech.models.wallpaperModels.requestBody.SearchParameters
import com.oyetech.models.wallpaperModels.requestBody.SearchRequestConsts
import com.oyetech.models.wallpaperModels.requestBody.toSearchMap
import com.oyetech.models.wallpaperModels.responses.WallpaperResponseData
import com.oyetech.navigation.navigationFuncs.WallpaperNavigationFunc
import com.oyetech.wallpaperList.paging.WallpaperListPagingFactory
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-7.02.2024-
-19:25-
 **/

class WallpaperListVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers),
    ImageViewerOverlayRepository {

    var wallpaperSearchResultLiveData = makeViewStateLiveData<List<WallpaperResponseData>>()

    val wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase by KoinJavaComponent.inject(
        WallpaperSearchOperationUseCase::class.java
    )

    val imageViewerRepository: ImageViewerRepository by KoinJavaComponent.inject(
        com.oyetech.domain.repository.viewHelperRepositories.ImageViewerRepository::class.java
    )

    lateinit var wallpaperListPagingFactory: WallpaperListPagingFactory
    lateinit var wallpaperPagedList: LiveData<PagedList<WallpaperResponseData>>

    var downloadWallpaperPermissionRequestLiveData = SingleLiveEvent<Boolean>()

    var lastSearchParams = SearchParameters()

    var sortingListKey = ""

    fun initWallpaperList(sortingKey: String?, queryString: String? = null) {
        if (sortingKey.isNullOrBlank()) {
            // TODO will be fixed for another operations.
        } else {
            if (sortingListKey == "") {
                // first init
                sortingListKey = sortingKey ?: ""
            } else {
                // already init, don't do anything.
                return
            }
        }



        lastSearchParams.sortingKey = sortingListKey
        lastSearchParams.q = queryString
        wallpaperSearchOperationUseCase.searchParameters.sortingKey = sortingListKey
        wallpaperSearchOperationUseCase.setSpecialQuery(queryString)

        if (::wallpaperListPagingFactory.isInitialized) {
            return
        }

        wallpaperListPagingFactory =
            WallpaperListPagingFactory(viewModelScope, wallpaperSearchOperationUseCase)
        wallpaperListPagingFactory.pageIndex = 0
        wallpaperPagedList =
            LivePagedListBuilder<Int, WallpaperResponseData>(
                wallpaperListPagingFactory,
                WallpaperListPagingFactory.pagedListConfig
            )
                .setInitialLoadKey(1)
                .build()
    }

    fun getInitalState(): LiveData<ViewState<ViewStatus>> {
        return wallpaperListPagingFactory.liveDataSource.switchMap {
            it.initialLoading
        }
    }

    fun getNetworkState(): LiveData<ViewState<ViewStatus>> {
        return wallpaperListPagingFactory.liveDataSource.switchMap {
            it.networkLoading
        }
    }

    fun invalidatePaging(isRefresh: Boolean = false): Boolean {
        Timber.d("before invalidddd == " + lastSearchParams.toSearchMap().toString())

        if (!isRefresh) {
            var isCanInvalidate = lastSearchParams?.let {
                wallpaperSearchOperationUseCase.isSearchParametersChanged(
                    it
                )
            } ?: false

            Timber.d("isCanInvalidate == " + isCanInvalidate)

            if (!isCanInvalidate) {
                return false
            } else {
                lastSearchParams = wallpaperSearchOperationUseCase.searchParameters.copy(page = 1)
            }
        }



        Timber.d("invalidateeeeeee")
        wallpaperListPagingFactory.pageIndex = 0
        wallpaperListPagingFactory.liveDataSource.value?.invalidate()
        return true
    }

    fun retry() {
        wallpaperListPagingFactory.liveDataSource.value?.retry()
    }

    fun setSearchQuery(queryString: String) {
        wallpaperSearchOperationUseCase.setSearchQuery(queryString)
    }

    fun getImageViewerListData(
    ): ArrayList<ImageViewerPropertyData> {
        var list = wallpaperPagedList.value?.map {
            var imageViewerPropertyData = ImageViewerPropertyData(
                imageUrl = it.path,
                thumbnailUrl = it.thumbs.large,
                imageId = it.id
            )

            imageViewerPropertyData
        }

        return ArrayList(list)
    }

    fun navigateToImageViewer(
        context: FragmentActivity,
        transitionImageView: ImageView,
        adapterPosition: Int,
    ) {

        var imageViewerPropertyDataArrayList = getImageViewerListData()

        imageViewerRepository.initImageViewerWithTransition(
            context = context,
            imageViewerPropertyList = imageViewerPropertyDataArrayList,
            adapterPosition = adapterPosition,
            transitionImageView = transitionImageView,
            overlayRepository = this
        )
    }

    override fun moreLikeThisButtonHandler(item: ImageViewerPropertyData?) {
        Timber.d("imageUrlrllrlrlr == " + item.toString())


        if (item == null) {
            // do nothing
            return
        }

        var tagQueryString = SearchRequestConsts.likeQueryString + item.imageId

        navigate(WallpaperNavigationFunc.actionToWallpaperFragmentWithQueryString(tagQueryString))
    }

    override fun requestDownloadPermission() {
        downloadWallpaperPermissionRequestLiveData.value = true
    }
}