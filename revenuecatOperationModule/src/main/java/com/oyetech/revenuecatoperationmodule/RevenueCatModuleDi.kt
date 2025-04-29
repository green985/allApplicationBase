package com.oyetech.revenuecatoperationmodule

import org.koin.dsl.module

/**
Created by Erdi Özbek
-17.12.2024-
-19:56-
 **/

object RevenueCatModuleDi {

    val revenueCatOperationModule = module {
        single { SubscriptionOperationHelper() }
    }
}
