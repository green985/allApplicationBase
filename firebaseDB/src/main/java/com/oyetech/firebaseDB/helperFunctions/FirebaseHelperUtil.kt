package com.oyetech.firebaseDB.helperFunctions

import com.google.firebase.firestore.FieldValue

/**
Created by Erdi Özbek
-24.12.2024-
-12:23-
 **/

object FirebaseHelperUtil {
    fun getFirebaseTimeStamp(): FieldValue {
        return FieldValue.serverTimestamp()
    }
}