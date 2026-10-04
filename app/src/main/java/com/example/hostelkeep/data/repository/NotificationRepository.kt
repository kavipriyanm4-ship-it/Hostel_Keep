package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "notifications"

    fun getNotificationsFlow(): Flow<List<Notification>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialNotifications)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
                        if (list.isNotEmpty()) {
                            trySend(list)
                        } else {
                            trySend(MockData.initialNotifications)
                        }
                    } else {
                        trySend(MockData.initialNotifications)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialNotifications)
            awaitClose {}
        }
    }

    suspend fun addNotification(notification: Notification): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(notification.id).set(notification).await()
            } catch (e: Exception) {
                if (!MockData.initialNotifications.any { it.id == notification.id }) {
                    MockData.initialNotifications.add(0, notification)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsRead(id: String): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(id).update("isRead", true).await()
            } catch (e: Exception) {
                val index = MockData.initialNotifications.indexOfFirst { it.id == id }
                if (index >= 0) {
                    MockData.initialNotifications[index] = MockData.initialNotifications[index].copy(isRead = true)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
