package com.example.hostelkeep.data.repository

import com.example.hostelkeep.model.Outpass
import com.example.hostelkeep.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OutpassRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = "outpasses"
    private val scansCollection = "outpass_scans"

    fun getOutpassesForHostel(hostelId: String): Flow<List<Outpass>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("hostelId", hostelId)
            .orderBy("appliedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.mapNotNull { it.toObject(Outpass::class.java) })
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getOutpassesForStudent(studentId: String): Flow<List<Outpass>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("studentId", studentId)
            .orderBy("appliedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.mapNotNull { it.toObject(Outpass::class.java) })
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getAllOutpassesAdmin(): Flow<List<Outpass>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .orderBy("appliedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.mapNotNull { it.toObject(Outpass::class.java) })
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun applyOutpass(outpass: Outpass): Result<Unit> {
        return try {
            val docRef = firestore.collection(collection).document()
            val newOutpass = outpass.copy(id = docRef.id)
            docRef.set(newOutpass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(outpassId: String, status: RequestStatus, verifiedBy: String): Result<Unit> {
        return try {
            firestore.collection(collection).document(outpassId)
                .update(
                    mapOf(
                        "status" to status.name,
                        "verifiedBy" to verifiedBy
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun scanQrCode(qrToken: String, securityId: String, scanType: String): Result<Boolean> {
        return try {
            val query = firestore.collection(collection).whereEqualTo("qrCodeData", qrToken).get().await()
            if (query.isEmpty) {
                return Result.failure(Exception("Outpass not found"))
            }

            val doc = query.documents.first()
            val outpass = doc.toObject(Outpass::class.java) ?: return Result.failure(Exception("Failed to parse outpass"))
            val outpassId = outpass.id

            // Check if already scanned in the same direction to prevent duplicate checkouts
            val scanId = "${outpassId}_${scanType}"
            val scanDocRef = firestore.collection(scansCollection).document(scanId)
            val scanDoc = scanDocRef.get().await()
            if (scanDoc.exists()) {
                return Result.failure(Exception("QR code already scanned for $scanType"))
            }

            firestore.runTransaction { transaction ->
                val outpassRef = firestore.collection(collection).document(outpassId)
                
                val updates = mutableMapOf<String, Any>()
                val timestamp = System.currentTimeMillis()

                if (scanType == "EXIT") {
                    if (outpass.status != RequestStatus.APPROVED) {
                        throw Exception("Outpass is not approved for exit.")
                    }
                    updates["status"] = RequestStatus.CHECKED_OUT.name
                    updates["exitTime"] = timestamp
                    updates["isCheckedOut"] = true
                } else if (scanType == "ENTRY") {
                    if (outpass.status != RequestStatus.CHECKED_OUT) {
                        throw Exception("Student has not checked out.")
                    }
                    updates["status"] = RequestStatus.RETURNED.name
                    updates["returnTime"] = timestamp
                    updates["isCheckedIn"] = true
                } else {
                    throw Exception("Invalid scan type")
                }

                transaction.update(outpassRef, updates)

                val scanData = hashMapOf(
                    "outpassId" to outpassId,
                    "securityId" to securityId,
                    "scanType" to scanType,
                    "timestamp" to timestamp
                )
                transaction.set(scanDocRef, scanData)
            }.await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
