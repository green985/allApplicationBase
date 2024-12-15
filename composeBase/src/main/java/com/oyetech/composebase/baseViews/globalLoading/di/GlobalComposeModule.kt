package com.oyetech.composebase.baseViews.globalLoading.di

import com.oyetech.composebase.baseViews.globalLoading.GlobalLoadingViewModel
import com.oyetech.composebase.baseViews.globalLoading.delegate.GlobalLoadingDelegate
import com.oyetech.composebase.baseViews.globalLoading.delegate.GlobalLoadingDelegateImp
import com.oyetech.composebase.baseViews.globalLoading.usecase.GlobalLoadingUseCase
import com.oyetech.composebase.baseViews.globalToolbar.GlobalToolbarUseCase
import com.oyetech.composebase.baseViews.globalToolbar.GlobalToolbarViewModel
import com.oyetech.composebase.baseViews.globalToolbar.GlobalToolbarViewModelWithDelegate
import com.oyetech.composebase.baseViews.globalToolbar.delegate.GlobalToolbarDelegate
import com.oyetech.composebase.baseViews.globalToolbar.delegate.GlobalToolbarDelegateImp
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-3.10.2024-
-23:55-
 **/

object GlobalComposeModule {
    val globalComposeModuleV = module {
        viewModelOf(::GlobalLoadingViewModel)
        single { GlobalLoadingUseCase() }
        single<GlobalLoadingDelegate> { GlobalLoadingDelegateImp() }

        viewModelOf(::GlobalToolbarViewModel)
        viewModelOf(::GlobalToolbarViewModelWithDelegate)
        single { GlobalToolbarUseCase() }
        single<GlobalToolbarDelegate> { GlobalToolbarDelegateImp() }
    }
}