package com.oyetech.watchAppFeatures

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.composebase.sharedScreens.stopwatch.StopwatchDurationScreenSetup
import com.oyetech.composebase.sharedScreens.stopwatch.StopwatchScreenSetup

fun EntryProviderScope<NavKey>.wearAppNavigation() {

    entry<AppRoute.StopwatchDurationScreen> {
        StopwatchDurationScreenSetup()
    }

    entry<AppRoute.StopwatchScreen> {
        StopwatchScreenSetup()
    }
}

