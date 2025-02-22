package com.oyetech.domain.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.UUID

class ActivityProviderUseCase(application: Application) {

    private var activeActivity: Activity? = null

    private var isLoggingActive = true

    var activityMutableStateFlow = MutableSharedFlow<Activity?>(replay = 1, extraBufferCapacity = 1)

    val activityOnResumeMutableStateFlow =
        MutableSharedFlow<Boolean?>(replay = 1, extraBufferCapacity = 1)

    init {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityCreated")
                }
                runBlocking {
                    activityMutableStateFlow.emit(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityStarted")
                }
            }

            override fun onActivityResumed(activity: Activity) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityResumed")
                }
                activityOnResumeMutableStateFlow.tryEmit(true)
                activeActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityPaused")
                }
                activityOnResumeMutableStateFlow.tryEmit(false)
                activeActivity = null
            }

            override fun onActivityStopped(activity: Activity) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityStopped")
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivitySaveInstanceState")
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (isLoggingActive) {
                    Timber.d("activityName = " + activity?.localClassName + "  status == onActivityDestroyed")
                }
                runBlocking {
                    activityMutableStateFlow.emit(activity)
                }
            }

        })
    }

    fun getCurrentActivity(): Activity? {
        return activeActivity
    }

    fun getActivityStateFlow() = activityMutableStateFlow.asSharedFlow()

    fun <I, O> registerActivityResultLauncher(
        activity: ComponentActivity,
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>,
    ): ActivityResultLauncher<I> {
        val key = UUID.randomUUID().toString()
        return activity.activityResultRegistry.register(key, contract, callback)
    }
}
