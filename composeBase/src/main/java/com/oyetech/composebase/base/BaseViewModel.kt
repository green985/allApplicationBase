package com.oyetech.composebase.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.helpers.eventNavigator.TestEventNavigator
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

open class BaseViewModel(private val dispatcher: AppDispatchers) : ViewModel() {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    val testEventNavigator: TestEventNavigator by KoinJavaComponent.inject(
        TestEventNavigator::class.java
    )

    fun getDispatcherIo() = dispatcher.io

    init {
        Timber.d("Init BaseViewModel == " + this.javaClass.name)
        viewModelScope.launch {
            testEventNavigator.eventFlow.collectLatest {
                Timber.d("Event received from class ${this@BaseViewModel.javaClass.name}: $it")
                onEvent(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared == " + this.javaClass.name)
    }

    open fun onEvent(event: Any) {
        Timber.d("onEvent == " + event.toString())
        // Handle events here
    }
}