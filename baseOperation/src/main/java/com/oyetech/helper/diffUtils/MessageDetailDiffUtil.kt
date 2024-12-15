package com.oyetech.helper.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleBookPropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleChapterPropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleVerseWithTextDetailData
import com.oyetech.models.entity.feed.FeedDataResponse
import com.oyetech.models.entity.language.country.CountryPropertyDataResponse
import com.oyetech.models.entity.language.world.LanguagePropertyDataResponse
import com.oyetech.models.entity.messages.MessageConversationDataResponse
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.user.UserConnectResponseData
import com.oyetech.models.entity.user.UserProfileImageResponseData
import com.oyetech.models.entity.userNotification.UserNotificationResponseData

/**
Created by Erdi Özbek®
-29.03.2022-
-14:36-
 **/

val messageDetailDiffUtil = object : DiffUtil.ItemCallback<MessageDetailDataResponse>() {
    override fun areItemsTheSame(
        oldItem: MessageDetailDataResponse,
        newItem: MessageDetailDataResponse,
    ): Boolean {
        return oldItem.rowId == newItem.rowId

        if (oldItem.messageId == 0L || newItem.messageId == 0L) {
            return false
        }

        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(
        oldItem: MessageDetailDataResponse,
        newItem: MessageDetailDataResponse,
    ): Boolean {
        // val result = oldItem.status == newItem.status
//        val result = oldItem.hashCode() == newItem.hashCode()
        val result = oldItem == newItem

        // Timber.d("diff util areContentsTheSame = " + result)
        // Timber.d("diff util areContentsTheSame = " + oldItem.hashCode())

        return result
    }
}

val messageConversationDiffUtil =
    object : DiffUtil.ItemCallback<MessageConversationDataResponse>() {
        override fun areItemsTheSame(
            oldItem: MessageConversationDataResponse,
            newItem: MessageConversationDataResponse,
        ): Boolean {
            return oldItem.conversationId == newItem.conversationId
        }

        override fun areContentsTheSame(
            oldItem: MessageConversationDataResponse,
            newItem: MessageConversationDataResponse,
        ): Boolean {
            return oldItem == newItem
            return oldItem.unreadMessageCount == newItem.unreadMessageCount
            return oldItem.maxMessageId == newItem.maxMessageId
        }
    }

val userFeedListDiffUtil =
    object : DiffUtil.ItemCallback<FeedDataResponse>() {
        override fun areItemsTheSame(
            oldItem: FeedDataResponse,
            newItem: FeedDataResponse,
        ): Boolean {
            return false
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: FeedDataResponse,
            newItem: FeedDataResponse,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val userNotificationDiffUtil =
    object : DiffUtil.ItemCallback<UserNotificationResponseData>() {
        override fun areItemsTheSame(
            oldItem: UserNotificationResponseData,
            newItem: UserNotificationResponseData,
        ): Boolean {
            return oldItem.notificationId == newItem.notificationId
        }

        override fun areContentsTheSame(
            oldItem: UserNotificationResponseData,
            newItem: UserNotificationResponseData,
        ): Boolean {
            return oldItem == newItem
        }
    }

val languageListDiffUtil =
    object : DiffUtil.ItemCallback<LanguagePropertyDataResponse>() {
        override fun areItemsTheSame(
            oldItem: LanguagePropertyDataResponse,
            newItem: LanguagePropertyDataResponse,
        ): Boolean {
            return false
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(
            oldItem: LanguagePropertyDataResponse,
            newItem: LanguagePropertyDataResponse,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val bibleVerseDiffUtil =
    object : DiffUtil.ItemCallback<BibleVerseWithTextDetailData>() {
        override fun areItemsTheSame(
            oldItem: BibleVerseWithTextDetailData,
            newItem: BibleVerseWithTextDetailData,
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: BibleVerseWithTextDetailData,
            newItem: BibleVerseWithTextDetailData,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val bibleBookDiffUtil =
    object : DiffUtil.ItemCallback<BibleBookPropertyResponseData>() {
        override fun areItemsTheSame(
            oldItem: BibleBookPropertyResponseData,
            newItem: BibleBookPropertyResponseData,
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: BibleBookPropertyResponseData,
            newItem: BibleBookPropertyResponseData,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val bibleChapterDiffUtil =
    object : DiffUtil.ItemCallback<BibleChapterPropertyResponseData>() {
        override fun areItemsTheSame(
            oldItem: BibleChapterPropertyResponseData,
            newItem: BibleChapterPropertyResponseData,
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: BibleChapterPropertyResponseData,
            newItem: BibleChapterPropertyResponseData,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val biblePropertyDiffUtil =
    object : DiffUtil.ItemCallback<BiblePropertyResponseData>() {
        override fun areItemsTheSame(
            oldItem: BiblePropertyResponseData,
            newItem: BiblePropertyResponseData,
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: BiblePropertyResponseData,
            newItem: BiblePropertyResponseData,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val countryListDiffUtil =
    object : DiffUtil.ItemCallback<CountryPropertyDataResponse>() {
        override fun areItemsTheSame(
            oldItem: CountryPropertyDataResponse,
            newItem: CountryPropertyDataResponse,
        ): Boolean {
            return false
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(
            oldItem: CountryPropertyDataResponse,
            newItem: CountryPropertyDataResponse,
        ): Boolean {
            return false
            return oldItem == newItem
        }
    }

val userConnectListDiffUtil =
    object : DiffUtil.ItemCallback<UserConnectResponseData>() {
        override fun areItemsTheSame(
            oldItem: UserConnectResponseData,
            newItem: UserConnectResponseData,
        ): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: UserConnectResponseData,
            newItem: UserConnectResponseData,
        ): Boolean {
            return oldItem == newItem
        }
    }

val userProfileImageDiffUtil =
    object : DiffUtil.ItemCallback<UserProfileImageResponseData>() {
        override fun areItemsTheSame(
            oldItem: UserProfileImageResponseData,
            newItem: UserProfileImageResponseData,
        ): Boolean {
            if (oldItem.isLoading || newItem.isLoading) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserProfileImageResponseData,
            newItem: UserProfileImageResponseData,
        ): Boolean {
            return oldItem == newItem
        }
    }
