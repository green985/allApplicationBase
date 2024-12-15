package com.oyetech.composebase.projectRadioFeature.screens.views.toolbar

import com.oyetech.composebase.R

sealed class RadioToolbarActionItems(private val resourceId: Int = R.drawable.ic_search) {

    fun getDrawableResource() = resourceId

    // search action item key and drawable resource id
    data class Search(val resourceId: Int) : RadioToolbarActionItems(resourceId)
    data class Timer(val resourceId: Int) : RadioToolbarActionItems(resourceId)
    data class Sort(val resourceId: Int) : RadioToolbarActionItems(resourceId)
    data class DeleteList(val resourceId: Int) : RadioToolbarActionItems(resourceId)
}