package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Visitor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class VisitorRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "visitors"

    fun getVisitorsFlow(): Flow<List<Visitor>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialVisitors)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Visitor::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialVisitors)
                        }
                    } else {
                        trySend(MockData.initialVisitors)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialVisitors)
            awaitClose {}
        }
    }

    suspend fun registerVisitor(visitor: Visitor): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(visitor.id).set(visitor).await()
            } catch (e: Exception) {
                if (!MockData.initialVisitors.any { it.id == visitor.id }) {
                    MockData.initialVisitors.add(0, visitor)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkoutVisitor(visitorId: String): Result<Unit> {
        return try {
            val timeDf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeStr = timeDf.format(Date())
            try {
                firestore.collection(collection).document(visitorId).update("exitTime", timeStr).await()
            } catch (e: Exception) {
                val index = MockData.initialVisitors.indexOfFirst { it.id == visitorId }
                if (index >= 0) {
                    MockData.initialVisitors[index] = MockData.initialVisitors[index].copy(exitTime = timeStr)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
