package com.oyetech.models.utils.states

import androidx.annotation.IntDef

/**
Created by Erdi Özbek
-6.06.2022-
-21:41-
 **/

@Target(AnnotationTarget.TYPE)
@IntDef(CONNECT_LIST_FOLLOWER, CONNECT_LIST_FOLLOWING, CONNECT_LIST_LIKED, CONNECT_LIST_BLOCKED)
@Retention(AnnotationRetention.SOURCE)
annotation class ConnectListTypes

const val CONNECT_LIST_FOLLOWER = 0
const val CONNECT_LIST_FOLLOWING = 1
const val CONNECT_LIST_LIKED = 2
const val CONNECT_LIST_BLOCKED = 3
