package com.oyetech.adshelper.di.impl

import android.view.View
import timber.log.Timber

/**
Created by Erdi Özbek
-23.01.2023-
-23:29-
 **/

abstract class AdsHelperBase {

    var adViewHashMap = HashMap<String, View>()

    fun setAdViewHashMapWithLogg(adViewHashMap: HashMap<String, View>) {
        Timber.d("ad views set")
        this.adViewHashMap = adViewHashMap
    }

    fun getAdViewListWithKeyList(adIdList: List<String>): ArrayList<View> {
        val adViewList = arrayListOf<View>()

        adIdList.forEach {
            val adView = adViewHashMap.get(it)
            if (adView != null) {
                adViewList.add(adView)
            }
        }

        return adViewList
    }
}
