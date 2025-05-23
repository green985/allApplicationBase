package com.oyetech.composebase.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.oyetech.composebase.navigator.Route
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.java.KoinJavaComponent
import timber.log.Timber

open class BaseViewModel(private val dispatcher: AppDispatchers) : ViewModel() {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val navigationRouteStateFlow = MutableStateFlow("")

    fun navigateTo(route: Route) {
        navigationRouteStateFlow.value = route.route
    }

    fun getDispatcherIo() = dispatcher.io

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared == " + this.javaClass.name)
    }
}