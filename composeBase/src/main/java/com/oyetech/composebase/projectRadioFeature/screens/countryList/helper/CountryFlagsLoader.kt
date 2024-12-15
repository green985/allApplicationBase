package com.oyetech.composebase.projectRadioFeature.screens.countryList.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import org.koin.java.KoinJavaComponent
import java.util.Locale

class CountryFlagsLoader {

    val context: Context by KoinJavaComponent.inject(
        Context::class.java
    )

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getFlag(countryCode: String?): Drawable? {
        if (countryCode != null) {
            val resources = context.resources
            val resourceName = "flag_" + countryCode.lowercase(Locale.getDefault())
            val resourceId = resources.getIdentifier(
                resourceName, "drawable",
                context.packageName
            )
            if (resourceId != 0) {
                return resources.getDrawable(resourceId)
            }
        }
        return null
    }

    fun getFlagResourceId(countryCode: String?): Int {
        if (countryCode != null) {
            val resources = context.resources
            val resourceName = "flag_" + countryCode.lowercase(Locale.getDefault())
            val resourceId = resources.getIdentifier(
                resourceName, "drawable",
                context.packageName
            )
            return resourceId
            /*
            if (resourceId != 0) {
                return resources.getDrawable(resourceId)
            }

             */
        }
        return 0
    }

}