package com.oyetech.composebase.experimental.commentWidget;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.experimental.commentScreen.CommentItemUiState
import com.oyetech.core.coroutineHelper.AppDispatchers

/**
Created by Erdi Özbek
-29.12.2024-
-22:12-
 **/

class CommentScreenWithContentIdVM(appDispatchers: AppDispatchers, private val contentId: String) :
    BaseViewModel(appDispatchers) {

    val uiState = CommentItemUiState()


}