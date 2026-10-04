package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Fee
import com.example.hostelkeep.model.FeeStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FeeRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "fees"

    fun getFeesFlow(): Flow<List<Fee>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialFees)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Fee::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialFees)
                        }
                    } else {
                        trySend(MockData.initialFees)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialFees)
            awaitClose {}
        }
    }

    suspend fun payFee(feeId: String): Result<Unit> {
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = df.format(Date())
            val receiptNo = "REC-${(10000..99999).random()}"
            try {
                firestore.collection(collection).document(feeId).update(
                    mapOf(
                        "status" to FeeStatus.PAID,
                        "paidDate" to today,
                        "receiptNo" to receiptNo
                    )
                ).await()
            } catch (e: Exception) {
                val index = MockData.initialFees.indexOfFirst { it.id == feeId }
                if (index >= 0) {
                    MockData.initialFees[index] = MockData.initialFees[index].copy(
                        status = FeeStatus.PAID,
                        paidDate = today,
                        receiptNo = receiptNo
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
