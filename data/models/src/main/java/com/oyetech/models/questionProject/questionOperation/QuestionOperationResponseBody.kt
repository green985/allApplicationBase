package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
Created by Erdi Özbek
-3.10.2025-
-18:53-
 **/

@Keep
data class QuestionOperationResponseBody(
    val questionId: String = "",
    val questionTitle: String = "",
    val questionType: QuestionType = QuestionType.SINGLE_CHOICE,
    val moderationStatus: ModerationStatus = ModerationStatus.PENDING,
    val isQuestionApproved: Boolean = false,

    // Taxonomy keys to classify question semantics (category/subCategory)
    val taxonomy: QuestionTaxonomyRef = QuestionTaxonomyRef(),

    // Extensible fields (kept minimal for now)
    val payload: QuestionPayload? = null,
    val options: List<QueOption> = emptyList(),
    val constraints: QueConstraints? = null,
    val tags: List<QueTag> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val version: Int = 1,

    @ServerTimestamp val createdAt: Date? = null,
)
