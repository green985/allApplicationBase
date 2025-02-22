package com.oyetech.models.utils.const

/**
Created by Erdi Özbek
-6.03.2022-
-00:53-
 **/

object HelperConstant {
    val FirebaseDB_TRANSACTION_TIMEOUT: Long = 30000L
    val QUOTES_PAGER_LIMIT = 20
    val SPAN_DUMMY_STRING: String = "∫"

    val Font_Size_Const = 8
    val Font_Size_Min_Multiplier: Float = 0.8f
    val Font_Size_Max_Multiplier = 1.4f

    val MAP_ANIMATE_SPEED: Long = 500
    val MAP_DEFAULT_ZOOM: Double = 20.0
    val HERE_PLACE_SIZE: Int = 100
    const val SNACKBAR_SHOW_TIME = 3000L

    const val APP_FIRST_OPEN_COUNT_THRESHOLD: Int = 6
    const val RECEIVED_MESSAGE_COUNT_THRESHOLD: Int = 10
    const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000
    const val HistoryDatabaseVersion: Int = 6
    const val RadioModelVersion: Int = 6
    const val RadioLastListDatabaseVersion: Int = 6
    const val RadioAllListVersion: Int = 1
    const val FirebaseMessagingVersion: Int = 2

    const val ANIM_TIME: Long = 300L
    const val BOTTOM_NAVIGATION_SHOW_ANIM_TIME: Int = 200
    const val AGE_MIN: Int = 18
    const val AGE_MAX: Int = 99

    const val EXOPLAYER_ERROR_RETRY_COUNT: Int = 7
    const val EXOPLAYER_ERROR_RETRY_FIRST_TRY_LONG: Int = 200
    const val EXOPLAYER_DURATION_CALCULATOR_TIME_TRIGGER = 250L
    const val SPLASH_DELAY_TIME = 1000L

    const val VOLUME_UP_DELAY: Long = 2000L

    const val GLIDE_RADIUS_VALUE: Int = 24
    const val IMAGE_HEIGHT_WIDTH_THRESHOLD: Int = 300

    const val DEFAULT_TIMEOUT = 30L
    const val REFRESH_TOKEN_FLAG_TIMEOUT: Long = 3000
    const val REQUEST_DELAY_LONG = 1000L
    const val SENDING_TYPING_OPERATION_TIMEOUT = 3000L
    const val SENDING_TYPING_VIEW_OPERATION_TIMEOUT = 5000L
    const val SENDING_ONLINE_OPERATION = 1000L * 60 * 1
    const val SENDING_PERIOD = 1000L * 10 * 1

    const val VIBRATE_TIME_MILIS: Long = 150

    const val TIMER_UPDATE_MILIS: Long = 100
    const val PROFILE_IMAGES_THRESHOLD: Int = 9
    const val LANGUAGE_EXPIRED_TIME: Long = 24
    const val REVIEW_ASK_EXPIRED_TIME: Long = 24 * 7
    const val SUBS_DIALOG_RESOW_USER_EXPIRED_TIME: Long = 24 * 3
    const val SOCKET_TIME_OUT_SEC: Long = 5
    const val SOCKET_RECONNECT_DELAY: Long = 2

    // 2022-03-05T21:08:26.2999121Z
    const val DATE_FORMAT_EXPIRE_TOKEN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DATE_FORMAT_SEPERATOR = "/"
    const val HOUR_TIME_FORMAT = "HH:mm"
    const val HOUR_SECOND_TIME_FORMAT = "HH:mm:ss"
    const val BIRTHDAY_DATE_FORMAT = "dd/MM/yyyy"
    const val EXAMPLE_M4A_URL = "https://filesamples.com/samples/audio/m4a/sample4.m4a"

    const val MESSAGE_LIST_PAGE_SIZE = 50
    const val MESSAGE_DETAIL_PAGE_SIZE = 100
    const val MESSAGE_DETAIL_PAGING_TRIGGER = 50
    const val MESSAGE_NOTIFICATION_CODE = 50451
    const val NORMAL_NOTIFICATION_CODE = 3131

    const val MESSAGE_LIST_VISIBLE_COUNT_THRESHOLD = 5

    const val TRY_COUNT = 3L

    const val TEXT_EMOJI_NO_CONTROL_THRESHOLD = 10
    const val TEXT_EMOJI_NO_SIZE_UP_THRESHOLD = 3
    const val TEXT_EMOJI_SINGLE = 1
    const val TEXT_EMOJI_TWO = 2
    const val TEXT_EMOJI_THERE = 3

    const val SMOOTH_SCROLL_SPEED = 0.08F

    const val KEYBOARD_DETECT_HEIGHT_PX = 500

    const val FADE_TIME = 150
    const val DEBOUNCE_TIME_LONG = 300L
    const val TYPING_ANIM_DELAY_TIME = 750L

    const val EXO_PLAYER_DURATION_CALCULATE_DELAY = 250L
    const val EXO_PLAYER_DURATION_CALCULATOR_DIV = 1000

    const val AUDIO_RECORD_MAX_SEC = 29
    const val AUDIO_RECORD_MIN_MILLISECOND = 500L

    const val HTTP_AUTH_ERROR = 401
    const val HTTP_NOT_ACCEPTABLE = 406
    const val HTTP_SUCCESS = 200
    const val HTTP_ERROR = 400

    const val APP_SUBS_DIALOG_SHOW_THRESHOLD = 4

    const val APPLICATION_APP_ID = "com.oyetech.holybible"

    var LOCAL_NOTIFICATION_ALARM_ID = 512355
    var LOCAL_NOTIFICATION_NOTIFY_CHANNEL = "Local Notifications"

    var hereRequestUrl = "https://discover.search.hereapi.com/v1/discover"
}
