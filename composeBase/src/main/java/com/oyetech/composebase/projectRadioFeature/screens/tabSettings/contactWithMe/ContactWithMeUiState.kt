package com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-13.12.2024-
-16:17-
 **/

data class ContactUIState(
    val name: String = "",
    val email: String = "",
    val message: String = "",
    val isEmailValid: Boolean = true,
    val isDescriptionEmpty: Boolean = false,
    val isContactWasSent: Boolean = false,
)

sealed class ContactUIEvent : BaseEvent() {
    data class UpdateName(val name: String) : ContactUIEvent()
    data class UpdateEmail(val email: String) : ContactUIEvent()
    data class UpdateMessage(val message: String) : ContactUIEvent()
    object Submit : ContactUIEvent()
}