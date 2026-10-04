package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AllocationRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun allocateBed(
        studentId: String,
        hostelId: String,
        roomId: String,
        bedId: String,
        allocatedBy: String
    ): Result<Unit> {
        return try {
            try {
                firestore.runTransaction { transaction ->
                    val studentRef = firestore.collection("students").document(studentId)
                    val studentSnap = transaction.get(studentRef)
                    if (!studentSnap.exists()) {
                        throw IllegalStateException("Student not found")
                    }
                    val student = studentSnap.toObject(Student::class.java)!!

                    if (student.roomId.isNotEmpty() && student.bedId.isNotEmpty()) {
                        throw IllegalStateException("Student already has a room allocated")
                    }

                    val bedRef = firestore.collection("beds").document(bedId)
                    val bedSnap = transaction.get(bedRef)
                    if (!bedSnap.exists()) {
                        throw IllegalStateException("Bed not found")
                    }
                    val bed = bedSnap.toObject(Bed::class.java)!!
                    if (bed.status != "AVAILABLE") {
                        throw IllegalStateException("Bed is no longer available")
                    }

                    val roomRef = firestore.collection("rooms").document(roomId)
                    val roomSnap = transaction.get(roomRef)
                    if (!roomSnap.exists()) {
                        throw IllegalStateException("Room not found")
                    }
                    val room = roomSnap.toObject(Room::class.java)!!
                    val occupiedBeds = room.occupiedBeds.takeIf { it > 0 } ?: room.occupied
                    if (occupiedBeds >= room.capacity) {
                        throw IllegalStateException("Room capacity is full")
                    }

                    transaction.update(bedRef, mapOf("status" to "OCCUPIED", "studentId" to studentId))

                    val newOccupiedBeds = occupiedBeds + 1
                    val newOccupants = room.occupants + student.name
                    transaction.update(roomRef, mapOf(
                        "occupiedBeds" to newOccupiedBeds,
                        "occupied" to newOccupiedBeds,
                        "occupants" to newOccupants,
                        "status" to if (newOccupiedBeds >= room.capacity) "OCCUPIED" else "PARTIALLY_OCCUPIED"
                    ))

                    transaction.update(studentRef, mapOf(
                        "hostelId" to hostelId,
                        "roomId" to roomId,
                        "bedId" to bedId,
                        "roomNo" to room.roomNo.ifEmpty { room.roomNumber },
                        "bedNo" to bed.bedNo.ifEmpty { bed.bedNumber }
                    ))

                    val allocationId = "alloc_${System.currentTimeMillis()}"
                    val allocationRef = firestore.collection("room_allocations").document(allocationId)
                    val allocation = RoomAllocation(
                        id = allocationId,
                        studentId = studentId,
                        admissionNumber = student.admissionNumber.ifEmpty { student.rollNo },
                        hostelId = hostelId,
                        roomId = roomId,
                        bedId = bedId,
                        allocatedBy = allocatedBy,
                        allocatedAt = System.currentTimeMillis(),
                        status = "ACTIVE"
                    )
                    transaction.set(allocationRef, allocation)

                    val logId = "log_${System.currentTimeMillis()}"
                    val logRef = firestore.collection("audit_logs").document(logId)
                    val auditLog = AuditLog(
                        id = logId,
                        action = "BED_ALLOCATED",
                        performedBy = allocatedBy,
                        timestamp = System.currentTimeMillis(),
                        details = mapOf("studentId" to studentId, "roomId" to roomId, "bedId" to bedId)
                    )
                    transaction.set(logRef, auditLog)
                }.await()
            } catch (e: Exception) {
                val student = MockData.initialStudents.find { it.id == studentId }
                    ?: throw IllegalStateException("Student not found")
                if (student.roomId.isNotEmpty() && student.bedId.isNotEmpty()) {
                    throw IllegalStateException("Student already has a room allocated")
                }
                val bed = MockData.initialBeds.find { it.id == bedId }
                    ?: throw IllegalStateException("Bed not found")
                if (bed.status != "AVAILABLE") {
                    throw IllegalStateException("Bed is no longer available")
                }
                val room = MockData.initialRooms.find { it.id == roomId }
                    ?: throw IllegalStateException("Room not found")
                if (room.occupiedBeds >= room.capacity) {
                    throw IllegalStateException("Room capacity is full")
                }

                val bedIdx = MockData.initialBeds.indexOf(bed)
                MockData.initialBeds[bedIdx] = bed.copy(status = "OCCUPIED", studentId = studentId)

                val roomIdx = MockData.initialRooms.indexOf(room)
                val newOcc = room.occupiedBeds + 1
                MockData.initialRooms[roomIdx] = room.copy(
                    occupiedBeds = newOcc,
                    occupied = newOcc,
                    occupants = room.occupants + student.name,
                    status = if (newOcc >= room.capacity) "OCCUPIED" else "PARTIALLY_OCCUPIED"
                )

                val studentIdx = MockData.initialStudents.indexOf(student)
                MockData.initialStudents[studentIdx] = student.copy(
                    hostelId = hostelId,
                    roomId = roomId,
                    bedId = bedId,
                    roomNo = room.roomNo,
                    bedNo = bed.bedNo
                )

                val alloc = RoomAllocation(
                    id = "alloc_${System.currentTimeMillis()}",
                    studentId = studentId,
                    admissionNumber = student.admissionNumber,
                    hostelId = hostelId,
                    roomId = roomId,
                    bedId = bedId,
                    allocatedBy = allocatedBy,
                    status = "ACTIVE"
                )
                MockData.initialAllocations.add(alloc)

                MockData.initialAuditLogs.add(
                    AuditLog(
                        id = "log_${System.currentTimeMillis()}",
                        action = "BED_ALLOCATED",
                        performedBy = allocatedBy,
                        details = mapOf("studentId" to studentId, "roomId" to roomId, "bedId" to bedId)
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun vacateBed(
        allocationId: String,
        studentId: String,
        roomId: String,
        bedId: String,
        vacatedBy: String = "Admin"
    ): Result<Unit> {
        return try {
            try {
                firestore.runTransaction { transaction ->
                    val allocRef = firestore.collection("room_allocations").document(allocationId)
                    val allocSnap = transaction.get(allocRef)
                    if (!allocSnap.exists()) {
                        throw IllegalStateException("Active room allocation not found")
                    }
                    val allocation = allocSnap.toObject(RoomAllocation::class.java)!!
                    if (allocation.status != "ACTIVE") {
                        throw IllegalStateException("Allocation is already vacated")
                    }

                    transaction.update(allocRef, "status", "VACATED")

                    val bedRef = firestore.collection("beds").document(bedId)
                    transaction.update(bedRef, mapOf("status" to "AVAILABLE", "studentId" to null))

                    val roomRef = firestore.collection("rooms").document(roomId)
                    val roomSnap = transaction.get(roomRef)
                    val room = roomSnap.toObject(Room::class.java)!!
                    val occupiedBeds = room.occupiedBeds.takeIf { it > 0 } ?: room.occupied
                    val newOccupiedBeds = maxOf(0, occupiedBeds - 1)

                    val studentRef = firestore.collection("students").document(studentId)
                    val studentSnap = transaction.get(studentRef)
                    val studentName = studentSnap.getString("name") ?: ""
                    val newOccupants = room.occupants.filter { it != studentName }

                    transaction.update(roomRef, mapOf(
                        "occupiedBeds" to newOccupiedBeds,
                        "occupied" to newOccupiedBeds,
                        "occupants" to newOccupants,
                        "status" to if (newOccupiedBeds == 0) "AVAILABLE" else "PARTIALLY_OCCUPIED"
                    ))

                    transaction.update(studentRef, mapOf(
                        "roomId" to "",
                        "bedId" to "",
                        "roomNo" to "",
                        "bedNo" to ""
                    ))

                    val logId = "log_${System.currentTimeMillis()}"
                    val logRef = firestore.collection("audit_logs").document(logId)
                    val auditLog = AuditLog(
                        id = logId,
                        action = "BED_VACATED",
                        performedBy = vacatedBy,
                        timestamp = System.currentTimeMillis(),
                        details = mapOf("studentId" to studentId, "roomId" to roomId, "bedId" to bedId)
                    )
                    transaction.set(logRef, auditLog)
                }.await()
            } catch (e: Exception) {
                val allocation = MockData.initialAllocations.find { it.id == allocationId }
                    ?: MockData.initialAllocations.find { it.studentId == studentId && it.status == "ACTIVE" }
                    ?: throw IllegalStateException("Active room allocation not found")

                val allocIdx = MockData.initialAllocations.indexOf(allocation)
                if (allocIdx >= 0) {
                    MockData.initialAllocations[allocIdx] = allocation.copy(status = "VACATED")
                }

                val bed = MockData.initialBeds.find { it.id == bedId }
                if (bed != null) {
                    val bedIdx = MockData.initialBeds.indexOf(bed)
                    MockData.initialBeds[bedIdx] = bed.copy(status = "AVAILABLE", studentId = null)
                }

                val room = MockData.initialRooms.find { it.id == roomId }
                val student = MockData.initialStudents.find { it.id == studentId }
                if (room != null) {
                    val roomIdx = MockData.initialRooms.indexOf(room)
                    val newOcc = maxOf(0, room.occupiedBeds - 1)
                    MockData.initialRooms[roomIdx] = room.copy(
                        occupiedBeds = newOcc,
                        occupied = newOcc,
                        occupants = room.occupants.filter { it != (student?.name ?: "") },
                        status = if (newOcc == 0) "AVAILABLE" else "PARTIALLY_OCCUPIED"
                    )
                }

                if (student != null) {
                    val studentIdx = MockData.initialStudents.indexOf(student)
                    MockData.initialStudents[studentIdx] = student.copy(
                        roomId = "",
                        bedId = "",
                        roomNo = "",
                        bedNo = ""
                    )
                }

                MockData.initialAuditLogs.add(
                    AuditLog(
                        id = "log_${System.currentTimeMillis()}",
                        action = "BED_VACATED",
                        performedBy = vacatedBy,
                        details = mapOf("studentId" to studentId, "roomId" to roomId, "bedId" to bedId)
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
