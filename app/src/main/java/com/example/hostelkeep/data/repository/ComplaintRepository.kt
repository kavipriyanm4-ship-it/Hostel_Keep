package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Complaint
import com.example.hostelkeep.model.ComplaintStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ComplaintRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "complaints"

    fun getComplaintsFlow(): Flow<List<Complaint>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialComplaints)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Complaint::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialComplaints)
                        }
                    } else {
                        trySend(MockData.initialComplaints)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialComplaints)
            awaitClose {}
        }
    }

    suspend fun addComplaint(complaint: Complaint): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(complaint.id).set(complaint).await()
            } catch (e: Exception) {
                if (!MockData.initialComplaints.any { it.id == complaint.id }) {
                    MockData.initialComplaints.add(0, complaint)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(complaintId: String, status: ComplaintStatus): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(complaintId).update("status", status).await()
            } catch (e: Exception) {
                val index = MockData.initialComplaints.indexOfFirst { it.id == complaintId }
                if (index >= 0) {
                    MockData.initialComplaints[index] = MockData.initialComplaints[index].copy(status = status)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
