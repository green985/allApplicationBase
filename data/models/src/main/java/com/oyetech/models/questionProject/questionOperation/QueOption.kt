package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class QueOption(
    val id: String = "",
    val text: String = "",
    val value: Double? = null,
    val order: Int = 0,
)

object QueOptionsValues {
    val yesOption = QueOption(id = "YES", text = "Yes", order = 0)
    val noOption = QueOption(id = "NO", text = "No", order = 1)

    val queYesNoQuestionOptionList = listOf(yesOption, noOption)

}