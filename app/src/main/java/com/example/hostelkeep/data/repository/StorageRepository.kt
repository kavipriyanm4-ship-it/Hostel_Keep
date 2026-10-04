package com.example.hostelkeep.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository {
    private val storage: FirebaseStorage get() = FirebaseStorage.getInstance()

    suspend fun uploadImage(uri: Uri, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadByteArray(data: ByteArray, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putBytes(data).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(uid: String, uri: Uri): Result<String> {
        val path = "users/$uid/profile/profile.jpg"
        return uploadImage(uri, path)
    }

    suspend fun uploadProfileImage(uid: String, bytes: ByteArray): Result<String> {
        val path = "users/$uid/profile/profile.jpg"
        return uploadByteArray(bytes, path)
    }
}
