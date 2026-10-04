package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.Bed
import com.example.hostelkeep.model.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val collection = "rooms"
    private val bedsCollection = "beds"
    private val allocationRepository = AllocationRepository()

    fun getRoomsFlow(): Flow<List<Room>> = callbackFlow {
        try {
            val subscription = firestore.collection(collection)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialRooms)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val rooms = snapshot.documents.mapNotNull { it.toObject(Room::class.java) }
                        if (rooms.isNotEmpty()) {
                            trySend(rooms)
                        } else {
                            trySend(MockData.initialRooms)
                        }
                    } else {
                        trySend(MockData.initialRooms)
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialRooms)
            awaitClose {}
        }
    }

    suspend fun getRoomById(roomId: String): Room? {
        return try {
            val doc = firestore.collection(collection).document(roomId).get().await()
            doc.toObject(Room::class.java) ?: MockData.initialRooms.find { it.id == roomId }
        } catch (e: Exception) {
            MockData.initialRooms.find { it.id == roomId }
        }
    }

    suspend fun addRoom(room: Room, numBeds: Int): Result<Unit> {
        return try {
            val roomId = room.id.ifEmpty { "room_${System.currentTimeMillis()}" }
            val finalRoom = room.copy(id = roomId, occupiedBeds = 0, occupied = 0, status = "AVAILABLE")

            try {
                val batch = firestore.batch()
                val roomRef = firestore.collection(collection).document(roomId)
                batch.set(roomRef, finalRoom)

                for (i in 1..numBeds) {
                    val bedId = "bed_${roomId}_$i"
                    val bed = Bed(
                        id = bedId,
                        roomId = roomId,
                        bedNo = "B$i",
                        bedNumber = "B$i",
                        status = "AVAILABLE",
                        studentId = null
                    )
                    val bedRef = firestore.collection(bedsCollection).document(bedId)
                    batch.set(bedRef, bed)
                }
                batch.commit().await()
            } catch (e: Exception) {
                MockData.initialRooms.add(finalRoom)
                for (i in 1..numBeds) {
                    val bedId = "bed_${roomId}_$i"
                    MockData.initialBeds.add(
                        Bed(
                            id = bedId,
                            roomId = roomId,
                            bedNo = "B$i",
                            bedNumber = "B$i",
                            status = "AVAILABLE",
                            studentId = null
                        )
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoom(room: Room, newCapacity: Int): Result<Unit> {
        return try {
            val roomId = room.id
            val existingBeds = try {
                val snap = firestore.collection(bedsCollection).whereEqualTo("roomId", roomId).get().await()
                snap.documents.mapNotNull { it.toObject(Bed::class.java) }
            } catch (e: Exception) {
                MockData.initialBeds.filter { it.roomId == roomId }
            }

            val currentCapacity = existingBeds.size
            val updatedRoom = room.copy(capacity = newCapacity)

            try {
                val batch = firestore.batch()
                val roomRef = firestore.collection(collection).document(roomId)
                batch.set(roomRef, updatedRoom, com.google.firebase.firestore.SetOptions.merge())

                if (newCapacity > currentCapacity) {
                    for (i in (currentCapacity + 1)..newCapacity) {
                        val bedId = "bed_${roomId}_$i"
                        val bed = Bed(
                            id = bedId,
                            roomId = roomId,
                            bedNo = "B$i",
                            bedNumber = "B$i",
                            status = "AVAILABLE",
                            studentId = null
                        )
                        batch.set(firestore.collection(bedsCollection).document(bedId), bed)
                    }
                } else if (newCapacity < currentCapacity) {
                    val availableBeds = existingBeds.filter { it.status == "AVAILABLE" }
                    val bedsToRemoveCount = currentCapacity - newCapacity
                    val bedsToDelete = availableBeds.take(bedsToRemoveCount)
                    for (bed in bedsToDelete) {
                        batch.delete(firestore.collection(bedsCollection).document(bed.id))
                    }
                }
                batch.commit().await()
            } catch (e: Exception) {
                val roomIdx = MockData.initialRooms.indexOfFirst { it.id == roomId }
                if (roomIdx >= 0) {
                    MockData.initialRooms[roomIdx] = updatedRoom
                }
                if (newCapacity > currentCapacity) {
                    for (i in (currentCapacity + 1)..newCapacity) {
                        MockData.initialBeds.add(
                            Bed(
                                id = "bed_${roomId}_$i",
                                roomId = roomId,
                                bedNo = "B$i",
                                bedNumber = "B$i",
                                status = "AVAILABLE",
                                studentId = null
                            )
                        )
                    }
                } else if (newCapacity < currentCapacity) {
                    val availableBeds = MockData.initialBeds.filter { it.roomId == roomId && it.status == "AVAILABLE" }
                    val bedsToRemoveCount = currentCapacity - newCapacity
                    val bedsToDelete = availableBeds.take(bedsToRemoveCount)
                    MockData.initialBeds.removeAll(bedsToDelete)
                }
            }

            updateRoomOccupancy(roomId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(roomId: String, isAdmin: Boolean): Result<Unit> {
        if (!isAdmin) {
            return Result.failure(IllegalStateException("Admin only permission required to delete rooms."))
        }
        return try {
            val beds = try {
                val snap = firestore.collection(bedsCollection).whereEqualTo("roomId", roomId).get().await()
                snap.documents.mapNotNull { it.toObject(Bed::class.java) }
            } catch (e: Exception) {
                MockData.initialBeds.filter { it.roomId == roomId }
            }

            val isOccupied = beds.any { it.status == "OCCUPIED" }
            if (isOccupied) {
                return Result.failure(IllegalStateException("Cannot delete this room because one or more beds are occupied."))
            }

            try {
                val batch = firestore.batch()
                batch.delete(firestore.collection(collection).document(roomId))
                for (bed in beds) {
                    batch.delete(firestore.collection(bedsCollection).document(bed.id))
                }
                batch.commit().await()
            } catch (e: Exception) {
                MockData.initialRooms.removeAll { it.id == roomId }
                MockData.initialBeds.removeAll { it.roomId == roomId }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBedsForRoom(roomId: String): Flow<List<Bed>> = callbackFlow {
        try {
            val subscription = firestore.collection(bedsCollection)
                .whereEqualTo("roomId", roomId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockData.initialBeds.filter { it.roomId == roomId })
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val beds = snapshot.documents.mapNotNull { it.toObject(Bed::class.java) }
                        if (beds.isNotEmpty()) {
                            trySend(beds)
                        } else {
                            trySend(MockData.initialBeds.filter { it.roomId == roomId })
                        }
                    } else {
                        trySend(MockData.initialBeds.filter { it.roomId == roomId })
                    }
                }
            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            trySend(MockData.initialBeds.filter { it.roomId == roomId })
            awaitClose {}
        }
    }

    suspend fun updateBed(bed: Bed): Result<Unit> {
        return try {
            try {
                firestore.collection(bedsCollection).document(bed.id).set(bed).await()
            } catch (e: Exception) {
                val idx = MockData.initialBeds.indexOfFirst { it.id == bed.id }
                if (idx >= 0) {
                    MockData.initialBeds[idx] = bed
                } else {
                    MockData.initialBeds.add(bed)
                }
            }
            updateRoomOccupancy(bed.roomId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoomOccupancy(roomId: String): Result<Unit> {
        return try {
            val beds = try {
                val snap = firestore.collection(bedsCollection).whereEqualTo("roomId", roomId).get().await()
                snap.documents.mapNotNull { it.toObject(Bed::class.java) }
            } catch (e: Exception) {
                MockData.initialBeds.filter { it.roomId == roomId }
            }

            val actualCapacity = if (beds.isNotEmpty()) beds.size else 1
            val occupiedBeds = beds.count { it.status == "OCCUPIED" }
            val isMaintenance = beds.isNotEmpty() && beds.all { it.status == "MAINTENANCE" }

            val status = when {
                isMaintenance -> "MAINTENANCE"
                occupiedBeds == 0 -> "AVAILABLE"
                occupiedBeds >= actualCapacity -> "FULL"
                else -> "PARTIALLY_OCCUPIED"
            }

            val roomRef = firestore.collection(collection).document(roomId)
            try {
                roomRef.update(
                    mapOf(
                        "capacity" to actualCapacity,
                        "occupiedBeds" to occupiedBeds,
                        "occupied" to occupiedBeds,
                        "status" to status
                    )
                ).await()
            } catch (e: Exception) {
                val roomIdx = MockData.initialRooms.indexOfFirst { it.id == roomId }
                if (roomIdx >= 0) {
                    val r = MockData.initialRooms[roomIdx]
                    MockData.initialRooms[roomIdx] = r.copy(
                        capacity = actualCapacity,
                        occupiedBeds = occupiedBeds,
                        occupied = occupiedBeds,
                        status = status
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkBedAvailability(bedId: String): Boolean {
        return try {
            val doc = firestore.collection(bedsCollection).document(bedId).get().await()
            val bed = doc.toObject(Bed::class.java)
            bed?.status == "AVAILABLE"
        } catch (e: Exception) {
            val bed = MockData.initialBeds.find { it.id == bedId }
            bed?.status == "AVAILABLE"
        }
    }

    suspend fun isRoomAvailable(roomId: String): Boolean {
        return try {
            val doc = firestore.collection(collection).document(roomId).get().await()
            val room = doc.toObject(Room::class.java)
            if (room != null) {
                val occupied = room.occupiedBeds.takeIf { it > 0 } ?: room.occupied
                occupied < room.capacity
            } else {
                false
            }
        } catch (e: Exception) {
            val room = MockData.initialRooms.find { it.id == roomId }
            room?.let { it.occupiedBeds < it.capacity } ?: false
        }
    }

    suspend fun allocateBed(studentId: String, hostelId: String, roomId: String, bedId: String, allocatedBy: String): Result<Unit> {
        val res = allocationRepository.allocateBed(studentId, hostelId, roomId, bedId, allocatedBy)
        updateRoomOccupancy(roomId)
        return res
    }

    suspend fun vacateBed(allocationId: String, studentId: String, roomId: String, bedId: String, vacatedBy: String): Result<Unit> {
        val res = allocationRepository.vacateBed(allocationId, studentId, roomId, bedId, vacatedBy)
        updateRoomOccupancy(roomId)
        return res
    }
}
