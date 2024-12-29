package com.oyetech.firebaseDB.firebaseDB.helper

import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

/**
Created by Erdi Özbek
-29.12.2024-
-18:03-
 **/

class FirebaseOnlineHelper() {
    private val firebaseFirestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()

    init {
        Timber.d("FirebaseOnlineHelper init")
        initOnlineHelper()
    }

    fun initOnlineHelper() {
        firebaseFirestore.collection(".info/connected").get().addOnSuccessListener {
            Timber.d("connected")
        }.addOnFailureListener {
            Timber.d("not connected")
        }

    }
}
