package com.oyetech.kmpfeatures.operator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FeatureOperator<State, Action, Effect> {
    val state: StateFlow<State>
    val effects: Flow<Effect>

    fun dispatch(action: Action)
}
