package com.oyetech.domain.useCases.wallpaperApp

import com.oyetech.domain.repository.wallpaperApp.WallpaperSearchOperationRepository
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags.ANIME
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags.GENERAL
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags.PEOPLE
import com.oyetech.models.wallpaperModels.requestBody.PurityTags
import com.oyetech.models.wallpaperModels.requestBody.SearchParameters
import com.oyetech.models.wallpaperModels.requestBody.SearchRequestConsts
import com.oyetech.models.wallpaperModels.requestBody.toSearchMap
import com.oyetech.models.wallpaperModels.responses.WallpaperSearchResponseData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
Created by Erdi Özbek
-11.02.2024-
-20:13-
 **/

class WallpaperSearchOperationUseCase(private var repository: WallpaperSearchOperationRepository) {

    var searchParameters = SearchParameters(page = 1)

    suspend fun getWallpaperListWithSearchParameters(pageIndex: Int = 1): Flow<WallpaperSearchResponseData> {
        searchParameters.page = pageIndex
        var searchMap = searchParameters.toSearchMap()

        return repository.getWallpapersWithSearchParameters(searchMap)
    }

    fun setSearchQuery(queryString: String?) {
        searchParameters.q = queryString
    }

    fun setFileTypeQuery(isJpg: Boolean, isChecked: Boolean) {
        Timber.d("setFileTypeQuery isJpg== " + isJpg)
        Timber.d("setFileTypeQuery isChecked== " + isChecked)

        if (!isChecked) {
            searchParameters.fileTypeQuery = null
            return
        }

        var queryString = StringBuilder(SearchRequestConsts.fileTypeQueryHeader)

        if (isJpg) {
            var fileType = SearchRequestConsts.fileTypeJpg
            if (isChecked) {
                queryString.append(fileType)
            } else {
                queryString.clear()
            }
        } else {
            var fileType = SearchRequestConsts.fileTypePng
            if (isChecked) {
                queryString.append(fileType)
            } else {
                queryString.clear()
            }
        }

        if (queryString.toString().isNotBlank()) {
            searchParameters.fileTypeQuery = queryString.toString()
        } else {
            searchParameters.fileTypeQuery = null
        }


    }

    fun purityTypeValueWithTag(purityTag: PurityTags, checked: Boolean) {

        when (purityTag) {
            PurityTags.NSFW -> {
                searchParameters.purityNSFW = if (checked) 1 else 0
            }

            PurityTags.SKETCHY -> {
                searchParameters.puritySketchy = if (checked) 1 else 0
            }

            PurityTags.SFW -> {
                searchParameters.puritySFW = if (checked) 1 else 0
            }
        }
    }

    fun categoryTypeValue(general: CategoryTags, checked: Boolean) {

        when (general) {

            GENERAL -> {
                searchParameters.categoryGeneral = if (checked) 1 else 0
            }

            PEOPLE -> {
                searchParameters.categoryPeople = if (checked) 1 else 0
            }

            ANIME -> {
                searchParameters.categoryAnime = if (checked) 1 else 0

            }
        }
    }

    fun orderTypeValue(isDesc: Boolean, isChecked: Boolean) {
        Timber.d("sortingTypeValue isDesc== " + isDesc)
        Timber.d("sortingTypeValue isChecked== " + isChecked)

        if (!isChecked) {
            searchParameters.order = null
            return
        }

        var query = ""


        if (isDesc) {
            var fileType = SearchRequestConsts.orderTypeDesc
            if (isChecked) {
                query = (fileType)
            } else {
                query = ""
            }
        } else {
            var fileType = SearchRequestConsts.orderTypeAsc
            if (isChecked) {
                query = (fileType)
            } else {
                query = ""
            }
        }

        if (query.isNotBlank()) {
            searchParameters.order = query
        } else {
            searchParameters.order = null
        }


    }

    fun colorTypeValueWithTag(colorString: String, isChecked: Boolean) {
        Timber.d("colorTypeValueWithTag == colorString" + colorString)
        Timber.d("colorTypeValueWithTag == isChecked" + isChecked)
        if (isChecked) {
            searchParameters.colors = colorString
        } else {
            searchParameters.colors = null
        }
    }

    fun resolutionTypeValue(resolutionText: String, isChecked: Boolean) {
        Timber.d("adjaksjdaskd " + resolutionText.replace(" ", ""))
        if (isChecked) {
            searchParameters.selectedResolution = resolutionText.replace(" ", "").replace("×", "x")
        } else {
            searchParameters.selectedResolution = null
        }
    }

    fun resolutionAtLeastExactlyTypeValue(isAtLeast: Boolean, isChecked: Boolean) {
        Timber.d("asdasdasd == " + isAtLeast)
        Timber.d("asdasdasd == " + isChecked)

        if (!isChecked) {
            return
        }

        searchParameters.isAtLeastSelected = isAtLeast

    }

    fun resolutionRatioTypeValue(ratio: String, isChecked: Boolean) {
        var ratioKey = SearchRequestConsts.getRatioRequestParam(ratio)


        if (isChecked) {
            searchParameters.selectedRatioList.add(ratioKey)
        } else {
            searchParameters.selectedRatioList.remove(ratioKey)
        }
    }

    fun isSearchParametersChanged(lastSearchParams: SearchParameters): Boolean {
        var lastSearchRequest = lastSearchParams.toSearchMap()
        var currentSearchRequest = searchParameters.toSearchMap()

        lastSearchRequest.remove("page")
        lastSearchRequest.remove("seed")
        currentSearchRequest.remove("page")
        currentSearchRequest.remove("seed")

        Timber.d("isSearchParametersChanged lastSerchREsult == " + lastSearchRequest.toString())
        Timber.d("isSearchParametersChanged lastSerchREsult == " + currentSearchRequest.toString())


        if (lastSearchRequest == currentSearchRequest) {
            return false
        } else {
            return true
        }
    }

    fun setSpecialQuery(queryString: String?) {
        if (queryString.isNullOrBlank()) {
            return
        }

        searchParameters.q = queryString


    }
}