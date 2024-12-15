package com.example.settingss.ui.settingsMain;

import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.navigation.navigationFuncs.WallpaperNavigationFunc

/**
Created by Erdi Özbek
-17/03/2024-
-15:51-
 **/

class SettingsMainVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    init {

    }

    fun navigateContactWithUs() {
        var directions = WallpaperNavigationFunc.actionToContactWithUsFragment()
        navigate(directions)
    }

}