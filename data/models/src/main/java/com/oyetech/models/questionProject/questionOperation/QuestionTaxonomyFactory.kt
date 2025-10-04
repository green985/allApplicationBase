package com.oyetech.models.questionProject.questionOperation

object QuestionTaxonomyFactory {

    /**
     * Build a QuestionOperationResponseBody by taxonomy keys, auto-filling default options/constraints.
     * Note: questionType remains YES_NO_QUESTION as a placeholder until more types are introduced.
     */
    fun buildQuestion(
        title: String,
        taxonomy: QuestionTaxonomyRef,
        questionId: String = "",
    ): QuestionOperationResponseBody {
        val category = categoryFromKey(taxonomy.categoryKey)
        val opts: List<QueOption> = when (category) {
            QuestionCategories.TWO_CHOICE -> {
                val sub = twoChoiceSubFromKey(taxonomy.subCategoryKey)
                sub?.let {
                    QuestionTaxonomyDefaults.defaultOptions(
                        QuestionCategories.TWO_CHOICE,
                        it
                    )
                } ?: emptyList()
            }

            QuestionCategories.THREE_CHOICE -> {
                val sub = threeChoiceSubFromKey(taxonomy.subCategoryKey)
                sub?.let {
                    QuestionTaxonomyDefaults.defaultOptions(
                        QuestionCategories.THREE_CHOICE,
                        it
                    )
                } ?: emptyList()
            }

            else -> emptyList()
        }

        val constraints = when (category) {
            QuestionCategories.TWO_CHOICE -> QuestionTaxonomyDefaults.defaultConstraints(
                QuestionCategories.TWO_CHOICE
            )

            QuestionCategories.THREE_CHOICE -> QuestionTaxonomyDefaults.defaultConstraints(
                QuestionCategories.THREE_CHOICE
            )

            QuestionCategories.MULTI_CHOICE -> QuestionTaxonomyDefaults.defaultConstraints(
                QuestionCategories.MULTI_CHOICE
            )

            QuestionCategories.SCALE -> QuestionTaxonomyDefaults.defaultConstraints(
                QuestionCategories.SCALE
            )

            QuestionCategories.OPEN_ENDED -> QuestionTaxonomyDefaults.defaultConstraints(
                QuestionCategories.OPEN_ENDED
            )

            else -> null
        }

        return QuestionOperationResponseBody(
            questionId = questionId,
            questionTitle = title,
            questionType = QuestionType.YES_NO_QUESTION, // placeholder for now
            taxonomy = taxonomy,
            options = opts,
            constraints = constraints,
            metadata = mapOf(
                "template" to "taxonomy_default",
                "categoryKey" to taxonomy.categoryKey,
                "subCategoryKey" to (taxonomy.subCategoryKey ?: ""),
            ),
            version = 1,
            createdAt = null,
        )
    }
}