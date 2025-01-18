package com.oyetech.radioeveryonee.main

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.helper.isDebug
import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper
import com.oyetech.languageimp.LanguageOperationHelper
import com.oyetech.radioeveryonee.main.koins.AppComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class BaseApplication : Application() {

    init {
    }

    val activityProviderUseCase: ActivityProviderUseCase by KoinJavaComponent.inject(
        ActivityProviderUseCase::class.java
    )

    val languageHelper: LocalLanguageHelper by KoinJavaComponent.inject(
        LocalLanguageHelper::class.java
    )

    val languageOperationHelper: LanguageOperationHelper by KoinJavaComponent.inject(
        LanguageOperationHelper::class.java
    )

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()
        if (this.isDebug()) {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        configureDi()
        languageOperationHelper.initLanguageData()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setAppContext(this)
        setApplicationn(this)


        activityProviderUseCase.hashCode()

    }

    // CONFIGURATION ---
    open fun configureDi() = startKoin {
        androidContext(this@BaseApplication)
        // androidLogger(Level.DEBUG)
        modules(AppComponent.appComponentt)
    }

    /*
        private fun upgradeSecurityProvider() {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                return
            }
            try {
                ProviderInstaller.installIfNeededAsync(this, object : ProviderInstallListener {
                    override fun onProviderInstalled() {
                    }

                    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
                    }
                })
            } catch (e: Exception) {

            }

        }

     */

    companion object {
        private lateinit var context: Context

        fun getAppContext(): Context {
            return context
        }
        /*
                fun deleteDatabase() {
                    GlobalScope.launchCustom {
                        NacDatabase.clearAllTable()
                    }
                    unloadKoinModules(filterModule)
                    loadKoinModules(filterModule)
                }

         */

        fun setAppContext(ctx: Context) {
            context = ctx
            //    sAnalytics = GoogleAnalytics.getInstance(context)
        }

        private lateinit var application: Application

        fun getApplicationn(): Application {
            return application
        }
        /*
                fun deleteDatabase() {
                    GlobalScope.launchCustom {
                        NacDatabase.clearAllTable()
                    }
                    unloadKoinModules(filterModule)
                    loadKoinModules(filterModule)
                }

         */

        fun setApplicationn(application: Application) {
            Companion.application = application
            //    sAnalytics = GoogleAnalytics.getInstance(context)
        }
        /*
        private var sTracker: Tracker? = null
        var sAnalytics: GoogleAnalytics? = null


        @Synchronized
        fun getAnalytics(): Tracker? {
            if (sTracker == null) {
                sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
            }

            return sTracker
        }

         */
    }
}