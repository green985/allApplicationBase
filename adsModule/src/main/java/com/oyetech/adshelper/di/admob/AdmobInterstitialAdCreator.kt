package com.oyetech.adshelper.di.admob

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oyetech.adshelper.di.BaseAdHelper
import com.oyetech.domain.helper.isDebug
import timber.log.Timber

/**
Created by Erdi Özbek
-11.02.2023-
-17:59-
 **/

class AdmobInterstitialAdCreator(private var activity: Activity) : BaseAdHelper() {

    private val TAG: String = "ApplicationAdHelper"

    var testInterstitialAdId = "ca-app-pub-3940256099942544/1033173712"

    private var mInterstitialAd: InterstitialAd? = null
    private var isErrorAdLoad = false

    override fun prepareAdHelper() {
        return

        val adRequest: AdRequest = AdRequest.Builder().build()
        if (activity.isDebug()) {
            adRequest.isTestDevice(activity)
        }

        activity.runOnUiThread {
            InterstitialAd.load(
                activity, testInterstitialAdId, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        isErrorAdLoad = false
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd
                        setCallbacks()
                        Timber.d("onAdLoaded")
                    }

                    override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                        // Handle the error
                        Timber.d("onAdFailedToLoad == " + loadAdError.message)

                        mInterstitialAd = null
                        isErrorAdLoad = true
                    }
                }
            )
        }
    }

    override fun setCallbacks() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
                prepareAdHelper()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    override fun showAds(): Boolean {
        Log.d("TAG", "interstitial show girdiiiii")



        if (mInterstitialAd != null) {
            Log.d("TAG", "shooooowwwww")
            mInterstitialAd!!.show(activity)
            return true
        } else {

            if (isErrorAdLoad) {
                prepareAdHelper()
            }
            return false
        }
    }


}