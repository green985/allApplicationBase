package com.oyetech.domain.repository.firebase

import com.oyetech.models.quotes.responseModel.QuoteResponseData
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-25.12.2024-
-00:58-
 **/

interface FirebaseQuotesOperationRepository {
    val saveListOperationState: MutableStateFlow<Boolean?>
    fun saveListWithNoTag(list: List<QuoteResponseData>)
}