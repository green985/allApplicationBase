package com.oyetech.models.quotes.enums

import androidx.annotation.Keep

/**
Created by Erdi Özbek
-1.02.2025-
-19:36-
 **/
@Keep
enum class AdviceQuoteStatusEnum {
    Rejected,
    Approved,
    Idle
}

// write string to enum translator
fun String.toAdviceQuoteStatusEnum(): AdviceQuoteStatusEnum {
    return when (this) {
        "Rejected" -> AdviceQuoteStatusEnum.Rejected
        "Approved" -> AdviceQuoteStatusEnum.Approved
        "Idle" -> AdviceQuoteStatusEnum.Idle
        else -> AdviceQuoteStatusEnum.Idle
    }
}