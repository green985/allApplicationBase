package com.oyetech.firebaseDB.firebaseDB.radio

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.radioProject.entity.firebase.RadioPlayingAnalyticsData
import timber.log.Timber

/**
Created by Erdi Özbek
-15.12.2024-
-01:24-
 **/

class RadioAnalyticsOperationRepositoryImp : RadioAnalyticsOperationRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun sendRadioPlayingAnalytics(radioPlayingData: RadioPlayingAnalyticsData) {
        firestore.collection(FirebaseDatabaseKeys.Radio_Playing_Time_Collection)
            .add(radioPlayingData)
            .addOnSuccessListener {
                Timber.d("Radio Playing Analytics Data : $radioPlayingData")
            }
            .addOnFailureListener {
                // Handle failure
                Timber.d("Radio Playing Analytics Data error " + it.message)
            }
    }
}