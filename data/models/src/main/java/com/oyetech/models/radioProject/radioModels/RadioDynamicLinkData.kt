package com.oyetech.models.radioProject.radioModels

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
Created by Erdi Özbek
-5.05.2023-
-17:15-
 **/

@Keep
@Parcelize
data class RadioDynamicLinkData(
    var radioStreamUrl: String = "",
    var radioName: String = ""
) : Parcelable