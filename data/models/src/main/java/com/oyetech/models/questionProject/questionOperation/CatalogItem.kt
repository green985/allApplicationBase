package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class CatalogItem(
    val formId: String,
    val title: String,
    val isCompleted: Boolean,
)
