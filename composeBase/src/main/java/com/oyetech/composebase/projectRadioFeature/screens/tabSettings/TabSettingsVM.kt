package com.oyetech.composebase.projectRadioFeature.screens.tabSettings

import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-13.12.2024-
-12:28-
 **/

class TabSettingsVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    val toolbarUiState = MutableStateFlow(
        RadioToolbarState(
            title = context.getString(R.string.nav_item_settings),
        )
    )

}