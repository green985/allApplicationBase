package com.oyetech.composebase.base

import android.content.Context
import androidx.lifecycle.ViewModel
import com.oyetech.tools.coroutineHelper.AppDispatchers
import org.koin.java.KoinJavaComponent
import timber.log.Timber

open class BaseViewModel(private val dispatcher: AppDispatchers) : ViewModel() {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    fun getDispatcherIo() = dispatcher.io

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared == " + this.javaClass.name)
    }

    open fun onEvent(event: Any) {
        Timber.d("onEvent == " + event.toString())
        // Handle events here
    }
}