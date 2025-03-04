package com.oyetech.firebaserealtime

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepositoryDeneme
import com.oyetech.models.entity.auth.TokenDataResponse
import com.oyetech.models.entity.auth.TokenDataResponse22
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-18.02.2025-
-19:41-
 **/

class FirebaseRealtimeHelperRepositoryDenemeImpl(private val firebaseDatabase: FirebaseDatabase) :
    FirebaseRealtimeHelperRepositoryDeneme {
    val userID = "deneneneuseisrurn"

    val userPath = "users/$userID/message"

    override fun idlee() {

    }

    override fun sendTestMessage() {
        val myRef = firebaseDatabase.getReference(userPath)

//        myRef.setValue("Hello, World!")
//        myRef.push()
        val token = TokenDataResponse(expires = "populo", issueAt = "euripidis", token = "laudem")
//        myRef.child("received444").push().setValue(token)
        sendTestMessageTransaction()
//        obserseValueEvent()
    }

    fun sendTestMessageTransaction() {
        val myRef = firebaseDatabase.getReference(userPath)
        val token = TokenDataResponse22(expires = "populo", issueAt = "euripidis", token = "laudem")

        myRef.child("received444").push().setValue(token).addOnCompleteListener {
            Timber.d("addOnCompleteListener")
            Timber.d("addOnCompleteListener == " + it.isComplete)
            Timber.d("addOnCompleteListener == " + it.isSuccessful)
        }.addOnSuccessListener {
            Timber.d("addOnSuccessListener")
        }.addOnFailureListener {
            Timber.d("addOnFailureListener")
            Timber.d("addOnFailureListener == " + it.message)
            Timber.d("addOnFailureListener == " + it.localizedMessage)
        }

//        myRef.child("received444").push().setValue(token)
    }

    fun removeValue() {
        val myRef = firebaseDatabase.getReference(userPath)
        myRef.child("received444")
    }

    /**
     * Child event sadece bir degisiklik oldugunda sana o degisikligi dondurur
     * daha performansli
     */
    override fun observeSomething() {
        return
        val myRef = firebaseDatabase.getReference(userPath)
        myRef.child("received444").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val value = snapshot.value
                Timber.d("ChildEventListener received444= onChildAdded $value")
                Timber.d("ChildEventListener received444= onChildAdded $previousChildName")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val value = snapshot.value
                Timber.d("ChildEventListener received444= onChildChanged $value")
                Timber.d("ChildEventListener received444= onChildChanged $previousChildName")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

                val value = snapshot.value
                Timber.d("ChildEventListener received444= onChildRemoved$value")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                val value = snapshot.value
                Timber.d("ChildEventListener received444= onChildMoved $value")
                Timber.d("ChildEventListener received444= onChildMoved $previousChildName")
            }

            override fun onCancelled(error: DatabaseError) {
                println("error = $error")
            }
        })

    }

    /**
     * Value event butun childlari alir ve bir degisiklik oldugunda tekrar alir
     * Yani butun verileri sana tekrar dondurur
     */
    fun obserseValueEvent() {
        val myRef = firebaseDatabase.getReference(userPath)
        myRef.child("received444").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val value = postSnapshot.value
                    Timber.w("firebaseDatabasevalue received444= $value")
                }
                GlobalScope.launch {
                    delay(10000)
//                    myRef.child("received444").removeValue()
//                    Timber.w("firebaseDatabasevalue received444= removed")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("error = $error")
            }
        })
    }
}
