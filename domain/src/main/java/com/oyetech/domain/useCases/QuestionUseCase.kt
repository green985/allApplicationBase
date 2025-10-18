package com.oyetech.domain.useCases

import com.oyetech.domain.repository.firebase.FirebaseQuestionAnswerRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class QuestionUseCase(
    private val questionRepository: FirebaseQuestionOperationRepository,
    private val answerRepository: FirebaseQuestionAnswerRepository,
) {
    private val _questionUpdatedEvent = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    ).apply {
        // Initialize with empty string to trigger initial load
        tryEmit("")
    }

    val questionUpdatedEvent: Flow<String> = _questionUpdatedEvent

    suspend fun emitQuestionUpdated(questionId: String) {
        _questionUpdatedEvent.emit(questionId)
    }

    fun getQuestionListWithUpdates(): Flow<List<QuestionOperationResponseBody>> {
        return combine(
            questionRepository.getQuestionList(),
            _questionUpdatedEvent,
        ) { questions, updatedQuestionId ->
            Timber.d("Question list update triggered. Updated ID: $updatedQuestionId")

            if (updatedQuestionId.isBlank()) {
                return@combine questions
            }

            // Fetch updated question and replace in list
            try {
                val updatedQuestion = fetchQuestionById(updatedQuestionId)
                val mutableList = questions.toMutableList()
                val index = mutableList.indexOfFirst { it.questionId == updatedQuestionId }

                if (index != -1 && updatedQuestion != null) {
                    mutableList[index] = updatedQuestion
                    Timber.d("Replaced question at index $index")
                }

                mutableList
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch updated question $updatedQuestionId")
                questions
            }
        }
    }

    fun overlayAnswers(
        questions: List<QuestionOperationResponseBody>,
        answers: List<QueAnswer>,
    ): List<QuestionOperationResponseBody> {
        val answerMap = answers.associateBy { it.questionId }
        return questions.map { question ->
            val answer = answerMap[question.questionId]
            if (answer != null) {
                // Store answer info in metadata for UI layer to use
                question.copy(
                    metadata = question.metadata + mapOf(
                        "isAnswered" to "true",
                        "selectedAnswer" to (answer.selectedOptionIds?.firstOrNull() ?: "")
                    )
                )
            } else {
                question
            }
        }
    }

    private suspend fun fetchQuestionById(questionId: String): QuestionOperationResponseBody? {
        return try {
            var result: QuestionOperationResponseBody? = null
            questionRepository.getQuestionById(questionId).collect { question ->
                result = question
            }
            Timber.d("Fetched updated question: $questionId -> $result")
            result
        } catch (e: Exception) {
            Timber.e(e, "Error fetching question $questionId")
            null
        }
    }
}
