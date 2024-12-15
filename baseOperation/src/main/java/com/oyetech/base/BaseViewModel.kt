package com.oyetech.base

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.handleErrors
import com.oyetech.core.coroutineHelper.launchCustom
import com.oyetech.core.coroutineHelper.triggerErrorEvent
import com.oyetech.core.utils.ResultEvent
import com.oyetech.core.utils.SingleLiveEvent
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.domain.useCases.AdsHelperUseCase
import com.oyetech.domain.useCases.AnalyticsOperationUseCase
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.states.ViewState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import timber.log.Timber

abstract class BaseViewModel(
    var dispatcher: AppDispatchers,
) : ViewModel() {

    companion object {
        var deleteDatabaseLiveData = SingleLiveEvent<Boolean>()

    }

    val sharedPrefRepositoryHelper: SharedHelperRepository by KoinJavaComponent.inject(
        SharedHelperRepository::class.java
    )

    val adsHelperUseCase: AdsHelperUseCase by KoinJavaComponent.inject(
        AdsHelperUseCase::class.java
    )

    val activityProviderUseCase: ActivityProviderUseCase by KoinJavaComponent.inject(
        ActivityProviderUseCase::class.java
    )

    val analyticsOperationUseCase: AnalyticsOperationUseCase by KoinJavaComponent.inject(
        AnalyticsOperationUseCase::class.java
    )
    val context: Context by KoinJavaComponent.inject<Context>(Context::class.java)

    var bottomNavigationNavigateLiveData = SingleLiveEvent<Int>()

    lateinit var adViewListLiveData: MutableLiveData<List<View>>

    val handler = CoroutineExceptionHandler { it, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
    val scope = CoroutineScope(dispatcher.io + Job() + handler)

    // FOR ERROR HANDLER
    protected val _snackbarError = MutableLiveData<ResultEvent<Int>>()
    val snackBarError: LiveData<ResultEvent<Int>> get() = _snackbarError

    // FOR NAVIGATION

    //    var intentNavigationDataLiveEvent = SingleLiveEvent<IntentNavigationData<*>>()
    var keyboardShownLiveEvent = SingleLiveEvent<Boolean>()
    var bottomNavigationReselectTriggerLiveData = SingleLiveEvent<Int>()
    var bottomVisibilityChangeLiveData = SingleLiveEvent<Boolean>()
    var adHelperInitSingleLiveEvent = MutableLiveData<Boolean>()
    var logoutOperationLiveData = makeViewStateLiveData<Boolean>(noLoading = true)

    var delayedLiveData = MutableLiveData<Boolean>()

    /**
     * Convenient method to handle navigation from a [ViewModel]
     */
    init {
        Timber.d(this.javaClass.toString() + " viewModel class init")
        viewModelScope.launch(dispatcher.io) {
            delay(HelperConstant.SPLASH_DELAY_TIME)
            delayedLiveData.postValue(true)
        }
    }


    fun navigate(directions: NavDirections) {
//        _navigation.value = ResultEvent(NavigationCommand.To(directions))

        // googlePlayOperationUseCase.showSubscriptionSellPageForOverAppUsage()
    }

    fun navigateUp() {
//        _navigation.value = ResultEvent(NavigationCommand.Back)
    }

    fun setBottomBarNavigationVisibility(isVisible: Boolean) {
        bottomVisibilityChangeLiveData.value = isVisible
    }

    fun <T> makeViewStateLiveData(noLoading: Boolean = false): MutableLiveData<ViewState<T>> {
        if (noLoading) {
            return MutableLiveData<ViewState<T>>()
        }

        return MutableLiveData<ViewState<T>>(ViewState.loading())
    }

    fun <T> makeSingleLiveData(): SingleLiveEvent<ViewState<T>> {
        return SingleLiveEvent<ViewState<T>>()
    }

    suspend fun <T> Flow<T>.makeRequest(
        successAction: (ViewState<T>) -> Unit,
    ): Job? {
        var job: Job? = null
        try {
            var requestFlow = this
            job = viewModelScope.launch(dispatcher.io + handler) {
                try {
                    flow<T> {
                        requestFlow.collect {
                            withContext(dispatcher.main) {
                                successAction.invoke(ViewState.success(it))
                            }
                        }
                    }.catch {
                        Timber.d("deneme catchhhh")
                        errorHandling(it, successAction)
                    }.collect()
                } catch (it: Exception) {
                    Timber.d("try catch inside")
                    errorHandling(it, successAction)
                }
            }
        } catch (e: Exception) {
            Timber.d("try catch outside")
            errorHandling(e, successAction)
        }

        return job
    }

    suspend fun <T> Flow<T>.makeRequestWithGlobalScope(
        successAction: (ViewState<T>) -> Unit,
    ): Job? {
        var job: Job? = null
        try {
            var requestFlow = this
            job = GlobalScope.launch(dispatcher.io + handler) {
                try {
                    flow<T> {
                        requestFlow.collect {
                            withContext(dispatcher.main) {
                                successAction.invoke(ViewState.success(it))
                            }
                        }
                    }.catch {
                        Timber.d("deneme catchhhh")
                        errorHandling(it, successAction)
                    }.collect()
                } catch (it: Exception) {
                    Timber.d("try catch inside")
                    errorHandling(it, successAction)
                }
            }
        } catch (e: Exception) {
            Timber.d("try catch outside")
            errorHandling(e, successAction)
        }

        return job
    }

    fun <T : Any> MutableLiveData<ViewState<T>>.getDataViewState(): T? {
        var data = this.value?.data
        return data
    }

    fun <T : Any> SingleLiveEvent<ViewState<T>>.getDataViewState(): T? {
        var data = this.value?.data
        return data
    }

    private suspend fun <T> errorHandling(e: Exception, successAction: (ViewState<T>) -> Unit) {
        e.printStackTrace()
        var errorText = ErrorMessage.fetchErrorMessage(e.message)
        // var errorText = e.message ?: "error"
        withContext(dispatcher.main) {
            successAction.invoke(ViewState.error(errorText))
        }
    }

    private suspend fun <T> errorHandling(e: Throwable, successAction: (ViewState<T>) -> Unit) {
        e.printStackTrace()
        var errorText = ErrorMessage.fetchErrorMessage(e.message)

//        var errorText = e.message ?: "error"
        withContext(dispatcher.main) {
            successAction.invoke(ViewState.error(errorText))
        }
    }

    fun <T> Flow<T>.onEachWithTryCatch(
        action: ((T) -> Unit),
    ): Job {
        val flow = this
        return viewModelScope.launchCustom {
            try {
                flow.onEach {
                    try {
                        action.invoke(it)
                    } catch (e: Exception) {
                        triggerErrorEvent(e, true)
                    }
                }.handleErrors().collect()
            } catch (e: Exception) {
                triggerErrorEvent(e, true)
            }
        }
    }

    /*
        fun controlAndShowAds(adHelper: ApplicationAdHelper?, command: NavigationCommand.To) {
            if (adHelper == null) {
                Timber.d("controlAndShowAds nullllllll")
            } else {
                Timber.d("command == " + command.directions.actionId)
                var controlAdsCanShow = controlNavigationAdsCanShow(command)
                if (controlAdsCanShow) {
                    Timber.d("controlAndShowAds show")
                    adHelper?.showAd()
                } else {
                    Timber.d("controlAndShowAds  cant show ")
                }
            }
        }

     *//*


     */

    fun handleIntent(intent: Intent?) {
        if (intent != null) {/*
            var intentNavigationData = deepLinkHelper.controlAndCreateIntentNavigationData(intent)
            deepLinkHelper.clearIntentData(intent)
            if (intentNavigationData != null) {
                Timber.d("intent navigation data == " + intentNavigationData.toString())
                intentNavigationDataLiveEvent.value = intentNavigationData
                return
            }

             */
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("vm on clear " + this.javaClass.name)
    }

    fun logEventWithKey(key: String) {
        // TODO will be fixed.
        //  analyticsOperationUseCase.logEventWithKey(key)
    }

    fun getAdsViewListWithId(vararg ids: String): List<View> {
        return adsHelperUseCase.getAdViewWithIdList(*ids)
    }

    suspend fun getAdsViewListWithIdStateFlow(vararg ids: String): SharedFlow<List<View>> {
        return adsHelperUseCase.getAdViewWithIdListWithStateFlow(*ids)
    }

    fun getAdsViewListWithIdLiveData(vararg ids: String): MutableLiveData<List<View>> {
        if (::adViewListLiveData.isInitialized) {
            return adViewListLiveData
        }
        adViewListLiveData = MutableLiveData<List<View>>()
        viewModelScope.launch(dispatcher.io) {
            adsHelperUseCase.getAdViewWithIdListWithStateFlow(*ids).onEach {
                adViewListLiveData.postValue(it)
            }.collect()
        }

        return adViewListLiveData
    }
}
