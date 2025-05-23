package com.oyetech.local.di

import com.oyetech.local.database.RadioAllListDatabase
import com.oyetech.local.database.RadioDatabase
import com.oyetech.local.database.RadioFavListDatabase
import com.oyetech.local.database.RadioHistoryDatabase
import com.oyetech.local.database.RadioLastListDatabase
import com.oyetech.local.database.contentDatabase.ContentLikeDatabase
import com.oyetech.local.database.messaging.MessagesAllDatabase
import com.oyetech.local.database.messaging.MessagesSendingDatabase
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
    private const val MESSAGE_SENDING_DATABASE = "MESSAGE_SENDING_DATABASE"
    private const val FIREBASE_MESSAGE_DATABASE = "RADIO_ALL_LIST_DATABASE"
    private const val CONTENT_OPERATION_DATABASE = "CONTENT_OPERATION_DATABASE"

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

        single(named(MESSAGE_SENDING_DATABASE)) {
            MessagesSendingDatabase.buildDatabase(
                androidContext()
            )
        }
        factory { (get(named(MESSAGE_SENDING_DATABASE)) as MessagesSendingDatabase).radioModelDao() }

        single(named(FIREBASE_MESSAGE_DATABASE)) {
            MessagesAllDatabase.buildDatabase(
                androidContext()
            )
        }
        factory { (get(named(FIREBASE_MESSAGE_DATABASE)) as MessagesAllDatabase).modelDao() }


        single(named(CONTENT_OPERATION_DATABASE)) {
            ContentLikeDatabase.buildDatabase(
                androidContext()
            )
        }
        factory { (get(named(CONTENT_OPERATION_DATABASE)) as ContentLikeDatabase).modelDao() }

    }
}

