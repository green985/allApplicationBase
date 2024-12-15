package com.example.settingss.ui.settingsMain;

import android.view.View
import com.oyetech.base.BaseFragment
import com.oyetech.cripto.privateKeys.ads.WallpaperAppAdsKey
import com.oyetech.cripto.stringKeys.WebSiteUrls
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.materialViews.helper.viewHelpers.toolbarHelper.SimpleToolbarHelperLayout
import com.oyetech.materialViews.old.layouts.settings.SettingsSimpleTextInfoLayout.SettingsSimpleTextInfoLayoutRepository
import com.oyetech.navigation.default.openDefaultWebBrowser
import com.oyetech.settingss.R
import com.oyetech.settingss.databinding.FragmentSettingsMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-17/03/2024-
-15:50-
 **/

class SettingsMainFragment : BaseFragment<FragmentSettingsMainBinding, SettingsMainVM>(),
    SettingsSimpleTextInfoLayoutRepository {

    companion object {
        fun newInstance() = SettingsMainFragment()
    }

    override val viewModel: SettingsMainVM by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_settings_main

    override fun prepareView() {
        SimpleToolbarHelperLayout.changeToolbarTitleCenter(
            binding.includeToolbar,
            title = WallpaperLanguage.SETTINGS,
            center = true,
            backButtonEnable = false
        )
        prepareViewProperty()
    }

    override fun initAdView() {
        val adViewLiveData =
            viewModel.getAdsViewListWithIdLiveData(WallpaperAppAdsKey.BANNER_MAIN_ID)

        adViewLiveData.removeObservers(viewLifecycleOwner)
        adViewLiveData.observe(viewLifecycleOwner) {
            Timber.d("getAdsViewListWithIdLiveData == ")
            val customAdView = binding.csAdView
            customAdView.initAdView(it.firstOrNull())
        }
    }

    override fun prepareObserver() {

    }

    private fun prepareViewProperty() {
        binding.csAdFree.setSettingsLayoutProperty(WallpaperLanguage.AD_FREE_USE)
        binding.csTerms.setSettingsLayoutProperty(WallpaperLanguage.TERMS_AND_CONDITIONS)
        binding.csPrivacyPolicy.setSettingsLayoutProperty(WallpaperLanguage.PRIVACY_POLICY)
        binding.csContactWithUs.setSettingsLayoutProperty(WallpaperLanguage.CONTACT_WITH_US)


        binding.csAdFree.setRepositoryToView(this)
        binding.csTerms.setRepositoryToView(this)
        binding.csPrivacyPolicy.setRepositoryToView(this)
        binding.csContactWithUs.setRepositoryToView(this)
    }

    override fun clickAction(view: View) {
        var viewId = view.id

        when (viewId) {
            R.id.csPrivacyPolicy -> {
                Timber.d("csPrivacyPolicy")
                requireContext().openDefaultWebBrowser(WebSiteUrls.Wallpaper_Privacy_Policy_URL)
            }

            R.id.csTerms -> {
                Timber.d("csTerms")
                requireContext().openDefaultWebBrowser(WebSiteUrls.Wallpaper_Terms_Conditions_URL)

            }

            R.id.csAdFree -> {
                Timber.d("csAdFree")
                // todo
            }

            R.id.csContactWithUs -> {
                Timber.d("csContactWithUs")
                viewModel.navigateContactWithUs()
            }

        }


    }


}