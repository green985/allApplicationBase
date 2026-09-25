package com.oyetech.kmpmodels.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteKMP {
    @Serializable
    data object Home : RouteKMP

    @Serializable
    data object Login : RouteKMP

    @Serializable
    data object DailyPager : RouteKMP

    @Serializable
    data object Diary : RouteKMP

    @Serializable
    data object OperatorExample : RouteKMP
}
