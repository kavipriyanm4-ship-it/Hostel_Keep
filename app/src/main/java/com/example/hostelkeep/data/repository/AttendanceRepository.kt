package com.example.hostelkeep.data.repository

import com.example.hostelkeep.model.Attendance
import com.example.hostelkeep.model.AttendanceStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "attendance"

    fun getAttendanceForHostel(hostelId: String, date: String): Flow<List<Attendance>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("hostelId", hostelId)
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Attendance::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getStudentAttendanceHistory(studentId: String): Flow<List<Attendance>> = callbackFlow {
        val subscription = firestore.collection(collection)
            .whereEqualTo("studentId", studentId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Attendance::class.java) }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun markAttendance(attendance: Attendance): Result<Unit> {
        return try {
            val documentId = "${attendance.studentId}_${attendance.date}"
            val finalAttendance = attendance.copy(id = documentId, attendanceId = documentId)
            firestore.collection(collection).document(documentId).set(finalAttendance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markBatchAttendance(records: List<Attendance>): Result<Unit> {
        return try {
            val batch = firestore.batch()
            for (record in records) {
                val documentId = "${record.studentId}_${record.date}"
                val finalRecord = record.copy(id = documentId, attendanceId = documentId)
                val docRef = firestore.collection(collection).document(documentId)
                batch.set(docRef, finalRecord)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStudentByRollNo(rollNo: String): com.example.hostelkeep.model.Student? {
        return try {
            val snapshot = firestore.collection("students")
                .whereEqualTo("rollNo", rollNo)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                snapshot.documents[0].toObject(com.example.hostelkeep.model.Student::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun markSingleAttendance(
        student: com.example.hostelkeep.model.Student,
        date: String,
        status: AttendanceStatus,
        markedBy: String,
        markedByRole: String
    ): Result<Unit> {
        val attendance = Attendance(
            studentId = student.id.ifEmpty { student.uid },
            hostelId = student.hostelId,
            roomId = student.roomId.ifEmpty { student.roomNo },
            bedId = student.bedId.ifEmpty { student.bedNo },
            studentName = student.name,
            rollNo = student.rollNo,
            date = date,
            status = status,
            markedBy = markedBy,
            markedByRole = markedByRole,
            markedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            updatedBy = markedBy
        )
        return markAttendance(attendance)
    }
}
