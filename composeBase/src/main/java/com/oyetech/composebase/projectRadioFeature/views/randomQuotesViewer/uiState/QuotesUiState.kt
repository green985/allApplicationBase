package com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer.uiState

import androidx.compose.ui.text.AnnotatedString

/**
Created by Erdi Özbek
-16.12.2024-
-23:14-
 **/

data class QuotesUiState(
    val text: String = "",
    val author: String = "",
    val authorImage: String = "",
    val htmlFormatted: String = "",
    val annotatedStringText: AnnotatedString = AnnotatedString(""),
//    var charCount: Int = 0,
//    val tag : String = ""

)