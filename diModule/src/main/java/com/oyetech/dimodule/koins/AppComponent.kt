package com.oyetech.dimodule.koins

import com.oyetech.adshelper.di.AdsHelperModule
import com.oyetech.composebase.di.ComposeMainModule
import com.oyetech.composebase.projectQuestionsFeature.QuestionProjectModule
import com.oyetech.exoplayermodule.di.ExoPlayerModuleDi
import com.oyetech.firebaseDB.di.FirebaseDBModule
import com.oyetech.firebaserealtime.di.FirebaseRealtimeModule
import com.oyetech.glidemodule.GlideModuleDi
import com.oyetech.googlelogin.di.GoogleSignInModule
import com.oyetech.languageimp.LanguageImplModule
import com.oyetech.local.di.LocalDatabaseModule
import com.oyetech.notificationmodule.di.FirebaseNotificationModule
import com.oyetech.remote.di.QuestionSupabaseRemote
import com.oyetech.remote.di.RandomOperationModuleDI
import com.oyetech.remote.di.dataSourceModule
import com.oyetech.repository.di.QuestionRepositoryModule
import com.oyetech.repository.di.RepositoryModule
import com.oyetech.reviewer.di.GoogleAppReviewerModule
import com.oyetech.tools.di.CommonsModule
import org.koin.dsl.module

/**
Created by Erdi Özbek
-14.09.2022-
-16:29-
 **/

object AppComponent {

    val appComponentt = listOf(
//        LanguageModuleDi.wallpaperLanguageModule,
        Modules.MainModule,
        KoinHelperInits.HelperModule,
        CommonsModule.module,
        LocalDatabaseModule.module,
        dataSourceModule,
        QuestionRepositoryModule.questionRepositoryModule,

        // Features modules
        // ImageViewerModuleDi.imageViewerModule,
        AdsHelperModule.adsHelperModulee,
        ComposeMainModule.composeMainModule1,

        RandomOperationModuleDI.createRemoteModuleForRandomOperation("https://at1.api.radio-browser.info/json/"),
        RepositoryModule.module,
        ExoPlayerModuleDi.exoPlayerModule,
        GlideModuleDi.glideModule,
        GoogleAppReviewerModule.googlePlayReviewerModule,
        FirebaseDBModule.firebaseDBModulee,
        FirebaseNotificationModule.module,

        GoogleSignInModule.googleSignInModulee,
        LanguageImplModule.languageImplModule,
        FirebaseRealtimeModule.module,

        QuestionProjectModule.module,
        QuestionSupabaseRemote.createQuestionSupabaseRemoteModule(),

        )
}

object Modules {

    private var BASE_SHARED_PREF_KEY = "BASE_SHARED_PREF_KEY"

    val MainModule = module {

    }
}
