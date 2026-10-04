package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Emergency
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EmergencyRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "emergencies"

    fun getEmergenciesFlow(): Flow<List<Emergency>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialEmergencies)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Emergency::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialEmergencies)
                        }
                    } else {
                        trySend(MockData.initialEmergencies)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialEmergencies)
            awaitClose {}
        }
    }

    suspend fun triggerSOS(emergency: Emergency): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(emergency.id).set(emergency).await()
            } catch (e: Exception) {
                if (!MockData.initialEmergencies.any { it.id == emergency.id }) {
                    MockData.initialEmergencies.add(0, emergency)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveEmergency(id: String): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(id).update("status", "Resolved").await()
            } catch (e: Exception) {
                val index = MockData.initialEmergencies.indexOfFirst { it.id == id }
                if (index >= 0) {
                    MockData.initialEmergencies[index] = MockData.initialEmergencies[index].copy(status = "Resolved")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
