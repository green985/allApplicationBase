package com.oyetech.kmpfeatures.example

import com.oyetech.kmpfeatures.operator.AndroidOperatorViewModel
import com.oyetech.kmpmodels.ui.event.OperatorExampleAction
import com.oyetech.kmpmodels.ui.event.OperatorExampleEffect
import com.oyetech.kmpmodels.ui.state.OperatorExampleUiState

class OperatorExampleViewModel :
    AndroidOperatorViewModel<OperatorExampleUiState, OperatorExampleAction, OperatorExampleEffect>(
        operatorFactory = { viewModelScope ->
            OperatorExampleOperator(operatorScope = viewModelScope)
        },
    )
