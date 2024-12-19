package com.oyetech.radioeveryonee.main.koins

import com.oyetech.adshelper.di.AdsHelperModule
import com.oyetech.composebase.baseViews.globalLoading.di.GlobalComposeModule
import com.oyetech.composebase.di.ComposeMainModule
import com.oyetech.core.di.CommonsModule
import com.oyetech.cripto.privateKeys.WallpaperAppFragmentArgs
import com.oyetech.domain.di.DomainModule
import com.oyetech.domain.di.RadioDomainModule
import com.oyetech.exoplayermodule.di.ExoPlayerModuleDi
import com.oyetech.firebaseDB.di.FirebaseDBModule
import com.oyetech.glidemodule.GlideModuleDi
import com.oyetech.helper.BaseUrlConfigHelper
import com.oyetech.languageModule.di.LanguageModuleDi
import com.oyetech.local.di.RadioLocalModuleDi
import com.oyetech.quotes.QuotesLocalModuleDi
import com.oyetech.radiooperationmodule.di.RadioOperationModuleDi
import com.oyetech.radioservice.di.RadioServiceModule
import com.oyetech.remote.di.RadioModuleDI
import com.oyetech.remote.di.WallpaperRemoteModule
import com.oyetech.remote.di.dataSourceModule
import com.oyetech.remote.quotesRemote.QuotesRemoteModule
import com.oyetech.repository.di.RadioRepositoryDI
import com.oyetech.repository.di.WallpaperRepositoryModule
import com.oyetech.repository.quotesImp.QuotesImpModule
import com.oyetech.reviewer.di.GoogleAppReviewerModule
import org.koin.dsl.module

/**
Created by Erdi Özbek
-14.09.2022-
-16:29-
 **/

object AppComponent {

    val appComponentt = listOf(
        LanguageModuleDi.wallpaperLanguageModule,
        Modules.MainModule,
        KoinHelperInits.HelperModule,
        CommonsModule.module,
        dataSourceModule,
        WallpaperRemoteModule.createWallpaperAppRemoteModule(WallpaperAppFragmentArgs.WALLPAPER_API_BASE_URL),
        WallpaperRepositoryModule.wallpaperRepositoryModule,
        DomainModule.wallpaperDomainModule,

        // Features modules
        // ImageViewerModuleDi.imageViewerModule,
        AdsHelperModule.adsHelperModulee,
        FirebaseDBModule.firebaseDBModulee,
        ComposeMainModule.composeMainModule1,
        GlobalComposeModule.globalComposeModuleV,

        RadioDomainModule.radioDomainModule,
        RadioModuleDI.createRemoteModule(BaseUrlConfigHelper.BASE_DOMAIN_RADIO),
        RadioRepositoryDI.repositoryModule,
        RadioLocalModuleDi.localModule,
        RadioOperationModuleDi.radioModule,
        ExoPlayerModuleDi.exoPlayerModule,
        RadioServiceModule.serviceModule,
        GlideModuleDi.glideModule,
        GoogleAppReviewerModule.googlePlayReviewerModule,

        QuotesImpModule.quoteImpModule,
        QuotesRemoteModule.createZenQuotesRemoteModule(),
        QuotesLocalModuleDi.localModule
//        RevenueCatModuleDi.revenueCatOperationModule
    )
}

object Modules {

    private var BASE_SHARED_PREF_KEY = "BASE_SHARED_PREF_KEY"

    val MainModule = module {

    }

}
