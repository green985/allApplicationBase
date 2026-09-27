package com.oyetech.kmpfeatures.operator

import com.oyetech.kmpdomain.error.ErrorMapper
import com.oyetech.kmpmodels.ui.state.UiError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseFeatureOperator<State, Action, Effect>(
    initialState: State,
    protected val operatorScope: CoroutineScope,
) : FeatureOperator<State, Action, Effect> {
    private val errorMapper = ErrorMapper()
    private val mutableState = MutableStateFlow(initialState)
    final override val state: StateFlow<State> = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    final override val effects: Flow<Effect> = mutableEffects.asSharedFlow()

    final override fun dispatch(action: Action) {
        handleAction(action)
    }

    protected abstract fun handleAction(action: Action)

    protected fun updateState(transform: State.() -> State) {
        mutableState.update(transform)
    }

    protected fun emitEffect(effect: Effect): Boolean = mutableEffects.tryEmit(effect)

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        operatorScope.launch(block = block)

    protected fun <T> executeOperation(
        onStart: State.() -> State,
        operation: suspend () -> T,
        onSuccess: State.(T) -> State,
        onError: State.(UiError) -> State,
    ): Job {
        updateState(onStart)
        return launch {
            try {
                val result = operation()
                updateState { onSuccess(result) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                updateState { onError(errorMapper.map(error)) }
            }
        }
    }
}
