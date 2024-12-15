package com.oyetech.composebase.projectRadioFeature.screens.views.toolbar

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-30.11.2024-
-18:39-
 **/

data class RadioToolbarState(
    val title: String = "",
    val showBackButton: Boolean = false,
    val onActionButtonClick: (() -> Unit)? = null,
    val actionButtonState: PersistentList<RadioToolbarActionItems> = persistentListOf(),

    val timeLeftBadge: Int = 0,
)

sealed class RadioToolbarEvent {
    data class BackButtonClick(val handleWithNavigation: Boolean) : RadioToolbarEvent()
    data class OnActionButtonClick(val actionItem: RadioToolbarActionItems) : RadioToolbarEvent()
}
