package com.oyetech.presentation

import android.app.Application
import com.oyetech.composebase.di.ComposeMainModule
import com.oyetech.composebase.projectQuestionsFeature.QuestionProjectModule
import com.oyetech.dimodule.BaseApplication
import com.oyetech.dimodule.koins.KoinHelperInits
import com.oyetech.languageimp.LanguageImplModule
import com.oyetech.languageimp.LanguageOperationHelper
import com.oyetech.repository.di.RepositoryModule
import com.oyetech.tools.contextHelper.isDebug
import com.oyetech.tools.di.CommonsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class WearApplication : Application() {

    private val languageOperationHelper: LanguageOperationHelper by KoinJavaComponent.inject(
        LanguageOperationHelper::class.java
    )

    override fun onCreate() {
        super.onCreate()
        if (this.isDebug()) {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        }
        configureDi()
        languageOperationHelper.initLanguageHelper(true)
        BaseApplication.setAppContext(this)
        BaseApplication.setApplicationn(this)
    }

    private fun configureDi() = startKoin {
        androidContext(this@WearApplication)
        modules(
            KoinHelperInits.HelperModule,
            CommonsModule.module,
            LanguageImplModule.languageImplModule,
            ComposeMainModule.composeMainModule1,
            RepositoryModule.module,
            QuestionProjectModule.module,
        )
    }
}

