package com.oyetech.domain.useCases

import android.app.Activity
import android.view.View
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.AdsHelperRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class AdsHelperUseCase(
    private var repository: AdsHelperRepository,
    private var activityProviderUseCase: ActivityProviderUseCase,
    private var dispatcher: com.oyetech.tools.coroutineHelper.AppDispatchers,
) {

    init {
        GlobalScope.launch(dispatcher.io) {
            delay(1000)
            activityProviderUseCase.getActivityStateFlow().collectLatest {
                Timber.d("activitiyyy provicdesdasd == " + it?.javaClass?.name)
                setActivityToAdHelper(it)
            }
        }
    }

    fun getAdViewWithIdList(vararg ids: String): List<View> {
        val adIdList = arrayListOf<String>()
        ids.forEach {
            adIdList.add(it)
        }


        return repository.getAdViewWithIdList(adIdList)
    }

    suspend fun getAdViewWithIdListWithStateFlow(vararg ids: String): SharedFlow<List<View>> {
        val adIdList = arrayListOf<String>()
        ids.forEach {
            adIdList.add(it)
        }

        return repository.getAdViewWithIdListWithStateFlow(adIdList)
    }

    fun setAdViewHashMapWithLog(adViewHashMap: HashMap<String, View>) {
        repository.setAdViewHashMapWithLog(adViewHashMap)
    }

    fun setActivityToAdHelper(activity: Activity?) {
        repository.setActivityToAdHelper(activity)
    }

    fun showInterstitialAds(): Boolean {
        return repository.showInterstitialAds()
    }

    fun adsLoadedFlowEvent(): StateFlow<Boolean>? {
        return repository.adsLoadedStateFlow()?.asStateFlow()
    }

    fun clearAdView() {
        repository.adsLoadedStateFlow()?.value = false
        repository.clearAdsView()
    }

}

