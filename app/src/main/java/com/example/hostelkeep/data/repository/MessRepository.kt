package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.MessMenu
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "mess_menu"

    fun getMessMenuFlow(): Flow<List<MessMenu>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialMessMenu)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(MessMenu::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialMessMenu)
                        }
                    } else {
                        trySend(MockData.initialMessMenu)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialMessMenu)
            awaitClose {}
        }
    }

    suspend fun submitFeedback(day: String, rating: Float, comment: String): Result<Unit> {
        return try {
            try {
                val query = firestore.collection(collection).whereEqualTo("dayOfWeek", day).get().await()
                if (!query.isEmpty) {
                    val doc = query.documents[0]
                    val menu = doc.toObject(MessMenu::class.java)
                    if (menu != null) {
                        val newTotal = menu.totalReviews + 1
                        val newRating = ((menu.rating * menu.totalReviews) + rating) / newTotal
                        doc.reference.update(mapOf("rating" to newRating, "totalReviews" to newTotal)).await()
                    }
                }
            } catch (e: Exception) {
                val index = MockData.initialMessMenu.indexOfFirst { it.dayOfWeek.equals(day, true) }
                if (index >= 0) {
                    val menu = MockData.initialMessMenu[index]
                    val newTotal = menu.totalReviews + 1
                    val newRating = ((menu.rating * menu.totalReviews) + rating) / newTotal
                    MockData.initialMessMenu[index] = menu.copy(rating = newRating, totalReviews = newTotal)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
