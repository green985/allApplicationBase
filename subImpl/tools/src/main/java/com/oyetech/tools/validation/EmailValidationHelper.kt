package com.oyetech.tools.validation

/**
Created by Erdi Özbek
-22.01.2024-
-17:43-
 **/

object EmailValidationHelper {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")

        return emailRegex.matches(email)
    }

    fun isValidEmailSectionEvenForEmpty(email: String?): Boolean {
        if (email.isNullOrEmpty())
            return true

        return isValidEmail(email)
    }
}