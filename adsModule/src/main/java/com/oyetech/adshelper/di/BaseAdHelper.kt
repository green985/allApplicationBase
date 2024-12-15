package com.oyetech.adshelper.di

abstract class BaseAdHelper {

    abstract fun prepareAdHelper()
    abstract fun setCallbacks()
    abstract fun showAds(): Boolean

    fun initClass() {
        prepareAdHelper()
        setCallbacks()
    }
}
