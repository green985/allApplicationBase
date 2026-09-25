package com.oyetech.kmpfeatures.di

import com.oyetech.kmpfeatures.example.OperatorExampleOperator
import com.oyetech.kmpfeatures.example.OperatorExampleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

actual val platformKoinModule = module {
    viewModel {
        OperatorExampleViewModel(
            operatorFactory = { operatorScope ->
                get<OperatorExampleOperator> {
                    parametersOf(operatorScope)
                }
            },
        )
    }
}
