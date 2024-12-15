package com.oyetech.adshelper.di.impl

import android.app.Activity
import android.view.View
import com.oyetech.adshelper.di.admob.AdmobBannerAdCreator
import com.oyetech.adshelper.di.admob.AdmobInterstitialAdCreator
import com.oyetech.domain.repository.AdsHelperRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import kotlin.system.measureTimeMillis

class AdsHelperRepositoryImp : AdsHelperBase(), AdsHelperRepository {

    var activityy: Activity? = null

    lateinit var admobBannerCreator: AdmobBannerAdCreator

    lateinit var admobInterstitialAdCreator: AdmobInterstitialAdCreator

    override fun getAdViewWithIdList(adIdList: List<String>): List<View> {
        val adViewList = arrayListOf<View>()

        val foundedAdViewList = admobBannerCreator.getAdViewListWithKeyList(adIdList)
        adViewList.addAll(foundedAdViewList)
        return adViewList
    }

    override suspend fun getAdViewWithIdListWithStateFlow(adIdList: List<String>): SharedFlow<List<View>> {
        val adViewList = arrayListOf<View>()
        while (!::admobBannerCreator.isInitialized) {
            delay(10)
        }
        val foundedAdViewList = admobBannerCreator.getAdViewListWithKeyListSuspend(adIdList)
        adViewList.addAll(foundedAdViewList)

        return MutableStateFlow(adViewList)
    }

    override fun setAdViewHashMapWithLog(adViewHashMap: HashMap<String, View>) {
        setAdViewHashMapWithLogg(adViewHashMap)
    }

    override fun adsLoadedStateFlow(): MutableStateFlow<Boolean>? {
        return admobBannerCreator.adsLoadedStateFlow
    }

    override fun showInterstitialAds(): Boolean {
        if (::admobInterstitialAdCreator.isInitialized) {
            return admobInterstitialAdCreator.showAds()
        }
        Timber.d("interstitial giremediiiiiiii")
        return false
    }

    override fun clearAdsView() {
        if (activityy != null) {
            initAdHelperClasses(activityy!!, clearedFlag = true)
        }
    }

    override fun setActivityToAdHelper(activity: Activity?) {
        Timber.d("setttotototototototo")
        this.activityy = activity

        if (activityy != null) {
            initAdHelperClasses(activityy!!)
        }
    }

    fun initAdHelperClasses(activity: Activity, clearedFlag: Boolean = false) {
        var ssss = measureTimeMillis {

            Timber.d("initAdHelperClasses")

            if (::admobBannerCreator.isInitialized) {
                return
            }
            admobBannerCreator = AdmobBannerAdCreator(activity)


            admobInterstitialAdCreator = AdmobInterstitialAdCreator(activity)
            admobInterstitialAdCreator.initClass()

            Timber.d("initAdHelperClasses")
        }
        Timber.d("ssssss =" + ssss)
    }
}
