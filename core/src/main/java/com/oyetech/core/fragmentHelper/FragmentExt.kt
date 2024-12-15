package com.oyetech.core.fragmentHelper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.FragmentManager

/**
Created by Erdi Özbek
-16.07.2022-
-14:12-
 **/

fun getFragmentManager(context: Context?): FragmentManager? {
    return when (context) {
        is AppCompatActivity -> context.supportFragmentManager
        is ContextThemeWrapper -> getFragmentManager(context.baseContext)
        else -> null
    }
}
