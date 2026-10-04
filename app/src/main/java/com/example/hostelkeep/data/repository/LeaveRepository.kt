package com.example.hostelkeep.data.repository

import com.example.hostelkeep.model.Leave
import com.example.hostelkeep.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LeaveRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "leave_requests"

    fun getLeavesForHostel(hostelId: String): Flow<List<Leave>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("hostelId", hostelId)
            .orderBy("requestedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Leave::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getLeavesForStudent(studentId: String): Flow<List<Leave>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("studentId", studentId)
            .orderBy("requestedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Leave::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { subscription.remove() }
    }
    
    fun getAllLeavesAdmin(): Flow<List<Leave>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .orderBy("requestedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Leave::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun applyLeave(leave: Leave): Result<Unit> {
        return try {
            val docRef = firestore.collection(collection).document()
            val newLeave = leave.copy(id = docRef.id, requestId = docRef.id)
            docRef.set(newLeave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(leaveId: String, status: RequestStatus, reviewedBy: String, comment: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "reviewedBy" to reviewedBy,
                "reviewedAt" to System.currentTimeMillis(),
                "reviewComment" to comment
            )
            firestore.collection(collection).document(leaveId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelLeave(leaveId: String): Result<Unit> {
        return try {
            firestore.collection(collection).document(leaveId).update("status", RequestStatus.CANCELLED.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
