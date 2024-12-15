package com.example.settingss.di

import com.example.settingss.ui.contactWithUs.ContactWithUsVM
import com.example.settingss.ui.settingsMain.SettingsMainVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-17/03/2024-
-15:48-
 **/

object SettingsModules {

    var settingsModule = module {
        viewModelOf(::SettingsMainVM)
        viewModelOf(::ContactWithUsVM)
    }

}