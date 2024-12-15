package com.example.settingss.ui.settingsMain

import com.oyetech.languageModule.keyset.WallpaperLanguage

/**
Created by Erdi Özbek
-17/03/2024-
-18:25-
 **/

fun SettingsMainFragment.prepareLayoutTextAndRepository() {
    var csPrivacyPolicy = binding.csPrivacyPolicy
    var csAdFree = binding.csAdFree
    var csContactWithUs = binding.csContactWithUs
    var csTerms = binding.csTerms

    csPrivacyPolicy.setSettingsLayoutProperty(WallpaperLanguage.PRIVACY_POLICY)
    csAdFree.setSettingsLayoutProperty(WallpaperLanguage.AD_FREE_USE)
    csContactWithUs.setSettingsLayoutProperty(WallpaperLanguage.CONTACT_WITH_US)
    csTerms.setSettingsLayoutProperty(WallpaperLanguage.TERMS_AND_CONDITIONS)


    csPrivacyPolicy.setRepositoryToView(this)
    csAdFree.setRepositoryToView(this)
    csContactWithUs.setRepositoryToView(this)
    csTerms.setRepositoryToView(this)

}