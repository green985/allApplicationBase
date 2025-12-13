package com.oyetech.domain.useCases

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

class AnswerUseCase(
    private val questionSupabaseRepository: QuestionSupabaseRepository,
) {
    private val _answersState = MutableStateFlow<List<QueAnswer>>(emptyList())
    val answersState: StateFlow<List<QueAnswer>> = _answersState

    fun getAnswersByUser(userId: String): Flow<List<QueAnswer>> {
        return questionSupabaseRepository.getAnswersByUser(userId).onEach { answers ->
            _answersState.value = answers
        }
    }

    suspend fun getAnswersByQuestion(questionId: String): Flow<List<QueAnswer>> {
        return questionSupabaseRepository.getAnswersByQuestion(questionId)
    }

    fun submitAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return questionSupabaseRepository.addAnswer(answer).onEach { submittedAnswer ->
            val current = _answersState.value.toMutableList().apply {
                removeAll { it.questionId == submittedAnswer.questionId && it.userId == submittedAnswer.userId }
                add(submittedAnswer)
            }
            _answersState.value = current
        }
    }

    fun updateAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return questionSupabaseRepository.updateAnswer(answer).onEach { updatedAnswer ->
            val current = _answersState.value.toMutableList().apply {
                removeAll { it.questionId == updatedAnswer.questionId && it.userId == updatedAnswer.userId }
                add(updatedAnswer)
            }
            _answersState.value = current
        }
    }

    fun deleteAnswer(userId: String, questionId: String): Flow<Unit> {
        return questionSupabaseRepository.deleteAnswer(userId, questionId).onEach {
            val current = _answersState.value.toMutableList().apply {
                removeAll { it.questionId == questionId && it.userId == userId }
            }
            _answersState.value = current
        }
    }
}
