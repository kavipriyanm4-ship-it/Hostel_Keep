package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StudentRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "students"

    fun getStudentsFlow(): Flow<List<Student>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialStudents)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val students = snapshot.documents.mapNotNull { it.toObject(Student::class.java) }
                        if (students.isNotEmpty()) {
                            trySend(students)
                        } else {
                            trySend(MockData.initialStudents)
                        }
                    } else {
                        trySend(MockData.initialStudents)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialStudents)
            awaitClose {}
        }
    }

    fun getStudentFlow(uid: String): Flow<Student?> = callbackFlow {
        if (uid.isEmpty()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }
        try {
            val subscription = firestore.collection(collection).document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        val mock = MockData.initialStudents.find { it.id == uid || it.uid == uid }
                        trySend(mock)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val student = snapshot.toObject(Student::class.java)
                        trySend(student)
                    } else {
                        val mock = MockData.initialStudents.find { it.id == uid || it.uid == uid }
                        trySend(mock)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            val mock = MockData.initialStudents.find { it.id == uid || it.uid == uid }
            trySend(mock)
            awaitClose {}
        }
    }

    suspend fun getStudentById(studentId: String): Student? {
        return try {
            val doc = firestore.collection(collection).document(studentId).get().await()
            doc.toObject(Student::class.java) ?: MockData.initialStudents.find { it.id == studentId || it.uid == studentId }
        } catch (e: Exception) {
            MockData.initialStudents.find { it.id == studentId || it.uid == studentId }
        }
    }

    suspend fun getStudentsByHostel(hostelId: String): Result<List<Student>> {
        return try {
            val snapshot = firestore.collection(collection).whereEqualTo("hostelId", hostelId).get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(Student::class.java) }
            if (list.isEmpty()) {
                val mockList = MockData.initialStudents.filter { it.hostelId == hostelId }
                Result.success(mockList)
            } else {
                Result.success(list)
            }
        } catch (e: Exception) {
            val mockList = MockData.initialStudents.filter { it.hostelId == hostelId }
            Result.success(mockList)
        }
    }

    suspend fun addStudent(student: Student): Result<Unit> {
        return try {
            val docId = student.id.ifEmpty { student.uid.ifEmpty { "s_${System.currentTimeMillis()}" } }
            try {
                firestore.collection(collection).document(docId).set(student).await()
            } catch (e: Exception) {
                if (!MockData.initialStudents.any { it.id == student.id || it.uid == student.uid }) {
                    MockData.initialStudents.add(student)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(student: Student): Result<Unit> {
        return try {
            val docId = student.id.ifEmpty { student.uid }
            try {
                firestore.collection(collection).document(docId).set(student).await()
            } catch (e: Exception) {
                val index = MockData.initialStudents.indexOfFirst { it.id == student.id || it.uid == student.uid }
                if (index >= 0) {
                    MockData.initialStudents[index] = student
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(studentId: String, updates: Map<String, Any>): Result<Unit> {
        return updateStudentProfile(studentId, updates)
    }

    suspend fun updateStudentProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val finalUpdates = updates.toMutableMap()
            finalUpdates["updatedAt"] = FieldValue.serverTimestamp()
            try {
                firestore.collection(collection).document(uid).update(finalUpdates).await()
            } catch (e: Exception) {
                val index = MockData.initialStudents.indexOfFirst { it.id == uid || it.uid == uid }
                if (index >= 0) {
                    val s = MockData.initialStudents[index]
                    val updated = s.copy(
                        name = finalUpdates["name"] as? String ?: s.name,
                        phone = finalUpdates["phone"] as? String ?: s.phone,
                        parentName = finalUpdates["parentName"] as? String ?: s.parentName,
                        parentPhone = finalUpdates["parentPhone"] as? String ?: s.parentPhone,
                        bloodGroup = finalUpdates["bloodGroup"] as? String ?: s.bloodGroup,
                        address = finalUpdates["address"] as? String ?: s.address,
                        profileImageUrl = finalUpdates["profileImageUrl"] as? String ?: s.profileImageUrl
                    )
                    MockData.initialStudents[index] = updated
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<Unit> {
        return try {
            try {
                firestore.collection(collection).document(studentId).delete().await()
            } catch (e: Exception) {
                MockData.initialStudents.removeAll { it.id == studentId || it.uid == studentId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
