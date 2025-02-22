package com.oyetech.local.di

import com.oyetech.local.database.RadioAllListDatabase
import com.oyetech.local.database.RadioDatabase
import com.oyetech.local.database.RadioFavListDatabase
import com.oyetech.local.database.RadioHistoryDatabase
import com.oyetech.local.database.RadioLastListDatabase
import com.oyetech.local.database.messaging.FirebaseMessagingMessageDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
Created by Erdi Özbek
-23.11.2022-
-19:07-
 **/

object RadioLocalModuleDi {

    private const val DATABASE = "DATABASE"
    private const val RADIO_HISTORY_DATABASE = "RADIO_HISTORY_DATABASE"
    private const val RADIO_LIST_DATABASE = "RADIO_LIST_DATABASE"
    private const val RADIO_FAV_LIST_DATABASE = "RADIO_FAV_LIST_DATABASE"
    private const val RADIO_ALL_LIST_DATABASE = "RADIO_ALL_LIST_DATABASE"
    private const val FIREBASE_MESSAGE_DATABASE = "RADIO_ALL_LIST_DATABASE"





    val localModule = module {
        single(named(DATABASE)) { RadioDatabase.buildDatabase(androidContext()) }
        factory { (get(named(DATABASE)) as RadioDatabase).radioModelDao() }
        // factory { (get(named(DATABASE)) as RadioDatabase).messageConversationDao() }

        single(named(RADIO_HISTORY_DATABASE)) { RadioHistoryDatabase.buildDatabase(androidContext()) }
        factory { (get(named(RADIO_HISTORY_DATABASE)) as RadioHistoryDatabase).radioModelDao() }

        single(named(RADIO_LIST_DATABASE)) { RadioLastListDatabase.buildDatabase(androidContext()) }
        factory { (get(named(RADIO_LIST_DATABASE)) as RadioLastListDatabase).radioLastListDao() }

        single(named(RADIO_FAV_LIST_DATABASE)) { RadioFavListDatabase.buildDatabase(androidContext()) }
        factory { (get(named(RADIO_FAV_LIST_DATABASE)) as RadioFavListDatabase).radioModelDao() }

        single(named(RADIO_ALL_LIST_DATABASE)) { RadioAllListDatabase.buildDatabase(androidContext()) }
        factory { (get(named(RADIO_ALL_LIST_DATABASE)) as RadioAllListDatabase).radioModelDao() }

        single(named(FIREBASE_MESSAGE_DATABASE)) {
            FirebaseMessagingMessageDatabase.buildDatabase(
                androidContext()
            )
        }
        factory { (get(named(FIREBASE_MESSAGE_DATABASE)) as FirebaseMessagingMessageDatabase).radioModelDao() }

    }
}

