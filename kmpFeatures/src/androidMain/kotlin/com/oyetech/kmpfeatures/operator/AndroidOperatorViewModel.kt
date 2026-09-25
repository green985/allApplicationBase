package com.oyetech.kmpfeatures.operator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class AndroidOperatorViewModel<State, Action, Effect>(
    operatorFactory: (CoroutineScope) -> FeatureOperator<State, Action, Effect>,
) : ViewModel(), FeatureOperator<State, Action, Effect> {
    private val operator = operatorFactory(viewModelScope)

    final override val state: StateFlow<State>
        get() = operator.state

    final override val effects: Flow<Effect>
        get() = operator.effects

    final override fun dispatch(action: Action) {
        operator.dispatch(action)
    }
}
