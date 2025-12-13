package com.oyetech.models.questionProject.questionOperation

fun QuestionListWithFilterResponse.toQuestionList(): List<QuestionOperationResponseBody> {
    return questions
}

fun QuestionListWithFilterResponse.toQueFilter(): QueFilter {
    return filters
}

fun List<QuestionOperationResponseBody>.toQuestionListWithFilterResponse(
    count: Int = this.size,
    filters: QueFilter = QueFilter.DEFAULT,
): QuestionListWithFilterResponse {
    return QuestionListWithFilterResponse(
        questions = this,
        count = count,
        filters = filters,
    )
}
