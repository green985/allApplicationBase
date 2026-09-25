package com.oyetech.kmpfeatures.navigation

import com.oyetech.kmpmodels.navigation.RouteKMP

object KmpFeaturesRoutes {
    fun path(route: RouteKMP): String = when (route) {
        RouteKMP.Home -> "home"
        RouteKMP.Login -> "login"
        RouteKMP.DailyPager -> "dailyPager"
        RouteKMP.Diary -> "diary"
        RouteKMP.OperatorExample -> "operatorExample"
    }
}
