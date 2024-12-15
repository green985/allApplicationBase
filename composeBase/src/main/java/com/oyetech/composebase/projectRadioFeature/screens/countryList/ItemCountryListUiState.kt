package com.oyetech.composebase.projectRadioFeature.screens.countryList

import androidx.annotation.DrawableRes

/**
Created by Erdi Özbek
-21.11.2024-
-20:56-
 **/

data class ItemCountryListUiState(
    val countryName: String,
    val countryFullName: String,
    val radioCount: Int,
    @DrawableRes
    val flagId: Int? = null,
)
