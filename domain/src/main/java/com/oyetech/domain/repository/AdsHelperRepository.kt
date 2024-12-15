package com.oyetech.domain.repository

import android.app.Activity
import android.view.View
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AdsHelperRepository {

    fun getAdViewWithIdList(adIdList: List<String>): List<View>

    suspend fun getAdViewWithIdListWithStateFlow(adIdList: List<String>): SharedFlow<List<View>>

    fun setAdViewHashMapWithLog(adViewHashMap: HashMap<String, View>) {

    }

    fun setActivityToAdHelper(activity: Activity?)
    fun showInterstitialAds(): Boolean

    fun clearAdsView()

    fun adsLoadedStateFlow(): MutableStateFlow<Boolean>? {
        return null
    }
}