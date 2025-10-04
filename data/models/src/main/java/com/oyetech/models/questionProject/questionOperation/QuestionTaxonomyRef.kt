package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QuestionTaxonomyRef(
    val categoryKey: String = "",
    val subCategoryKey: String? = null,
    val taxonomyVersion: Int = 1,
)