package com.oyetech.firebaseDB.firebaseDB.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserRepository

/**
Created by Erdi Özbek
-16.02.2025-
-17:51-
 **/

class FirebaseMessagingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) : FirebaseMessagingRepository {

    override fun idlee() {

    }
}

interface FirebaseMessagingRepository {
    fun idlee()
}
