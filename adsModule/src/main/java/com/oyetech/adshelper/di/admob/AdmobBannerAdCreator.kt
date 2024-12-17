package com.oyetech.adshelper.di.admob

import android.annotation.SuppressLint
import android.app.Activity
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.oyetech.domain.helper.isDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Arrays

/**
Created by Erdi Özbek
-11.02.2023-
-17:00-
 **/

@SuppressLint("MissingPermission")
class AdmobBannerAdCreator(private var activity: Activity) {

    var testAppId = "ca-app-pub-3940256099942544/6300978111"

    var adViewHashMap = HashMap<String, View>()

    val isErrorLogOpen = true

    val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(activity, 320)

    var adsLoadedStateFlow = MutableStateFlow(false)

    private val adSizeBanner: AdSize
        get() {
            activity.apply {

                val display =
                    ContextCompat.getSystemService(this, DisplayManager::class.java)?.displays?.get(
                        0
                    )
                val outMetrics = DisplayMetrics()
                display?.getMetrics(outMetrics)

                val density = outMetrics.density

                var adWidthPixels = outMetrics.widthPixels.toFloat()

                val adWidth = (adWidthPixels / density).toInt()
                return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
            }
        }

    init {

        val builder = RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList("34C1F81B1A54BC3736143DFC37E97E61"))
            .setTestDeviceIds(Arrays.asList("B9EAE6B15305085C64C0646951586EB5"))
            .setTestDeviceIds(Arrays.asList("8B78B0A76FD8B878ED7441185194C491"))
            .build()
        activity.also {
            //MobileAds.initialize(activity)
            MobileAds.setRequestConfiguration(builder)
        }

    }

    private suspend fun createAdViewWithAdViewIdSuspend(adViewId: String): View {
        // todo will be fixed.

        val bannerView = AdView(activity)
        bannerView.adUnitId = testAppId

        //TODO will be organized for another ad size
        bannerView.setAdSize(adSizeBanner)
        /*
        if (adViewId == "radioo") {
        } else {
            bannerView.setAdSize(adSize)
        }

         */

// Step 3: Load an ad.

        val adRequest = AdRequest.Builder()
            .build()
        if (activity.isDebug()) {
            adRequest.isTestDevice(activity)
        }
        setCallbacks(adViewId, bannerView)

        withContext(Dispatchers.Main) {
            bannerView.loadAd(adRequest)
        }

        return bannerView
    }

    private fun createAdViewWithAdViewId(adViewId: String): View {
        // todo will be fixed.

        val bannerView = AdView(activity)
        bannerView.adUnitId = testAppId
        //TODO will be organized for another ad size
        bannerView.setAdSize(adSizeBanner)
        /*
        if (adViewId == "radioo") {
        } else {
            bannerView.setAdSize(adSize)
        }

         */

// Step 3: Load an ad.

        val adRequest = AdRequest.Builder()
            .build()
        if (activity.isDebug()) {
            adRequest.isTestDevice(activity)
        }
        setCallbacks(adViewId, bannerView)

        bannerView.loadAd(adRequest)


        return bannerView
    }

    suspend fun getAdViewListWithKeyListSuspend(adIdList: List<String>): ArrayList<View> {
        var adViewList = arrayListOf<View>()

        adIdList.forEach {
            var adIdTmp = if (activity.isDebug()) {
                //testAppId
                it
            } else {
                it
            }

            var adView = adViewHashMap.get(adIdTmp)
            if (adView != null) {
                adViewList.add(adView)
            } else {
                var newAdView = createAdViewWithAdViewIdSuspend(adIdTmp)
                adViewHashMap[adIdTmp] = newAdView
                adViewList.add(newAdView)
            }
        }

        return adViewList

    }

    fun getAdViewListWithKeyList(adIdList: List<String>): ArrayList<View> {
        var adViewList = arrayListOf<View>()

        adIdList.forEach {
            var adIdTmp = if (activity.isDebug()) {
                testAppId
            } else {
                it
            }

            var adView = adViewHashMap.get(adIdTmp)
            if (adView != null) {
                adViewList.add(adView)
            } else {
                var newAdView = createAdViewWithAdViewId(adIdTmp)
                adViewHashMap[adIdTmp] = newAdView
                adViewList.add(newAdView)
            }
        }

        return adViewList

    }

    fun setCallbacks(adViewId: String, adView: AdView) {
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.d("onAdFailedToLoad     ===" + adViewId)
                if (isErrorLogOpen)
                    Timber.d("onAdFailedToLoad     ===" + adError.message)

                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                Timber.d("onAdImpression     ===" + adViewId)
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Timber.d("onAdLoaded     ===" + adViewId)
                if (adViewId == "radioo") {
                    adsLoadedStateFlow.value = true
                }


                adView.visibility = View.VISIBLE
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }


}