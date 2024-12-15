package com.oyetech.composebase.di

import com.oyetech.composebase.helpers.adViewDelegate.AdViewOperationDelegate
import com.oyetech.composebase.helpers.adViewDelegate.AdViewOperationDelegateImpl
import com.oyetech.composebase.helpers.vibrationHelper.IVibrationHelper
import com.oyetech.composebase.helpers.vibrationHelper.VibrationHelperImpl
import com.oyetech.composebase.projectRadioFeature.helper.RadioListSortRepositoryImp
import com.oyetech.composebase.projectRadioFeature.screens.countryList.CountryVM
import com.oyetech.composebase.projectRadioFeature.screens.languageList.LanguageVM
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioListVM
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm.RadioPlayerVM
import com.oyetech.composebase.projectRadioFeature.screens.radioSearchList.RadioSearchVM
import com.oyetech.composebase.projectRadioFeature.screens.tabAllList.RadioAllListFragmentVM
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.TabSettingsVM
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactViewModel
import com.oyetech.composebase.projectRadioFeature.screens.tagList.TagListVM
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerViewModel
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.IRadioFavViewModelSlice
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.IRadioPlayerViewModelSlice
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.RadioFavViewModelSliceImp
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.RadioPlayerViewModelSliceImp
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-30.09.2024-
-23:15-
 **/

object ComposeMainModule {
    val composeMainModule1 = module {
        single<IVibrationHelper> { VibrationHelperImpl(get()) }
        factory<AdViewOperationDelegate> { AdViewOperationDelegateImpl() }
//        viewModelOf(::RadioListViewModel)
        viewModelOf(::RadioListVM)
//        factory(::RadioListVM)
//        factory(::RadioListVM)

        viewModelOf(::RadioAllListFragmentVM)
        viewModelOf(::LanguageVM)
        viewModelOf(::RadioPlayerVM)
        viewModelOf(::CountryVM)
        viewModelOf(::RadioCountTimerViewModel)
        viewModelOf(::RadioSearchVM)
        viewModelOf(::TabSettingsVM)
        viewModelOf(::ContactViewModel)
        viewModelOf(::TagListVM)

        single<IRadioPlayerViewModelSlice> { RadioPlayerViewModelSliceImp(get(), get()) }
        single<IRadioFavViewModelSlice> { RadioFavViewModelSliceImp(get()) }
        single<RadioListSortRepository> { RadioListSortRepositoryImp() }
    }
}