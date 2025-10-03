package com.example.settingss.ui.settingsMain

import com.oyetech.languageModule.keyset.LanguageKey

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

    csPrivacyPolicy.setSettingsLayoutProperty(LanguageKey.privacyPolicy)
    csAdFree.setSettingsLayoutProperty(LanguageKey.adFreeUse)
    csContactWithUs.setSettingsLayoutProperty(LanguageKey.contactWithUs)
    csTerms.setSettingsLayoutProperty(LanguageKey.termsAndConditions)


    csPrivacyPolicy.setRepositoryToView(this)
    csAdFree.setRepositoryToView(this)
    csContactWithUs.setRepositoryToView(this)
    csTerms.setRepositoryToView(this)

}
