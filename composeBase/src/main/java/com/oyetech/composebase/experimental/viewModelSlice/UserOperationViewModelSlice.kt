package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.repository.firebase.FirebaseUserRepository

/**
Created by Erdi Özbek
-24.12.2024-
-19:39-
 **/

class UserOperationViewModelSlice(private val userRepository: FirebaseUserRepository) {

    context(BaseViewModel)
    fun deleteUser(uid: String) {
        userRepository.deleteUser(uid)
    }


}