package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.ComplaintStatus
import com.example.hostelkeep.model.Maintenance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MaintenanceRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "maintenance"

    fun getMaintenanceFlow(): Flow<List<Maintenance>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialMaintenance)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Maintenance::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialMaintenance)
                        }
                    } else {
                        trySend(MockData.initialMaintenance)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialMaintenance)
            awaitClose {}
        }
    }

    suspend fun addMaintenance(maintenance: Maintenance): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(maintenance.id).set(maintenance).await()
            } catch (e: Exception) {
                if (!MockData.initialMaintenance.any { it.id == maintenance.id }) {
                    MockData.initialMaintenance.add(0, maintenance)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: String, status: ComplaintStatus): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(id).update("status", status).await()
            } catch (e: Exception) {
                val index = MockData.initialMaintenance.indexOfFirst { it.id == id }
                if (index >= 0) {
                    MockData.initialMaintenance[index] = MockData.initialMaintenance[index].copy(status = status)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
