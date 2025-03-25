package com.oyetech.composebase.projectQuotesFeature.navigation

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.Route
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.QuoteMessageTab
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.QuoteSettingsTab
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.QuoteUserListTab
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.TabQuoteAppHomepage
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.FacSettings
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.MessageConversationList
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.QuoteAppHomepage
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.UserList
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-6.01.2025-
-23:25-
 **/

object QuoteAppProjectRoutes {

    val FacSettings = Route("FacSettings")
    val QuoteAdviceScreen = Route("quoteAdviceScreen")
    val QuoteAppHomepage = Route("quoteAppHomepage")
    val QuoteDetailRoute = Route("quoteDetail")
    val QuoteAuthorList = Route("quoteAuthorList")
    val QuoteSettings = Route("quoteSettings")
    val AdviceQuoteDebug = Route("adviceQuoteDebug")
    val ContactScreen = Route("quote/ContactScreen")
    val CompleteProfileScreen = Route("LoginOperationScreen/CompleteProfileScreen")
    val LoginOperationScreen = Route("LoginOperationScreen")
    val SearchScreen = Route("quote/SearchScreen")
    val MessageDetail = Route("quote/MessageDetail")
    val MessageConversationList = Route("quote/MessageConversationList")
    val UserList = Route("quote/UserList")
    val UserProfile = Route("quote/UserProfile")

    val quoteApplicationBottomTabNavList = listOf(
        TabQuoteAppHomepage, QuoteSettingsTab, QuoteMessageTab, QuoteUserListTab
    )
}

sealed class QuoteAppNavItem {
    object TabQuoteAppHomepage :
        BottomNavigationItem(
            path = QuoteAppHomepage.route.toString(),
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object QuoteSettingsTab :
        BottomNavigationItem(
            path = FacSettings.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object QuoteMessageTab :
        BottomNavigationItem(
            path = MessageConversationList.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object QuoteUserListTab :
        BottomNavigationItem(
            path = UserList.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

}
