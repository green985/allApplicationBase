package com.oyetech.composebase.projectQuotesFeature.views.toolbar

import com.oyetech.composebase.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class QuoteToolbarState(
    val title: String = "",
    val showBackButton: Boolean = false,
    val onActionButtonClick: (() -> Unit)? = null,
    val actionButtonState: PersistentList<QuoteToolbarActionItems> = persistentListOf(),

    val timeLeftBadge: Int = 0,
)

sealed class QuoteToolbarEvent {
    data class BackButtonClick(val handleWithNavigation: Boolean) : QuoteToolbarEvent()
    data class OnActionButtonClick(val actionItem: QuoteToolbarActionItems) : QuoteToolbarEvent()
}

sealed class QuoteToolbarActionItems(private val resourceId: Int = R.drawable.ic_search) {
    fun getDrawableResource() = resourceId

    // search action item key and drawable resource id
    data class Search(val resourceId: Int) : QuoteToolbarActionItems(resourceId)
    data class Timer(val resourceId: Int) : QuoteToolbarActionItems(resourceId)
    data class EditProfile(val resourceId: Int) : QuoteToolbarActionItems(resourceId)
    data class Sort(val resourceId: Int) : QuoteToolbarActionItems(resourceId)
    data class DeleteList(val resourceId: Int) : QuoteToolbarActionItems(resourceId)
}