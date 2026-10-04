package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HostelRepository {
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    // Hostels
    fun getHostelsFlow(): Flow<List<Hostel>> = callbackFlow {
        val subscription = firestore.collection("hostels")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(MockData.initialHostels)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Hostel::class.java) } ?: emptyList()
                trySend(if (list.isNotEmpty()) list else MockData.initialHostels)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getHostel(hostelId: String): Hostel? {
        if (hostelId.isEmpty()) return null
        return try {
            val doc = firestore.collection("hostels").document(hostelId).get().await()
            doc.toObject(Hostel::class.java) ?: MockData.initialHostels.find { it.id == hostelId }
        } catch (e: Exception) {
            MockData.initialHostels.find { it.id == hostelId }
        }
    }

    suspend fun getRoom(roomId: String): Room? {
        if (roomId.isEmpty()) return null
        return try {
            val doc = firestore.collection("rooms").document(roomId).get().await()
            doc.toObject(Room::class.java) ?: MockData.initialRooms.find { it.id == roomId }
        } catch (e: Exception) {
            MockData.initialRooms.find { it.id == roomId }
        }
    }

    suspend fun getBed(bedId: String): Bed? {
        if (bedId.isEmpty()) return null
        return try {
            val doc = firestore.collection("beds").document(bedId).get().await()
            doc.toObject(Bed::class.java) ?: MockData.initialBeds.find { it.id == bedId }
        } catch (e: Exception) {
            MockData.initialBeds.find { it.id == bedId }
        }
    }

    suspend fun addHostel(hostel: Hostel): Result<Unit> = try {
        firestore.collection("hostels").document(hostel.id).set(hostel).await()
        Result.success(Unit)
    } catch (e: Exception) {
        if (!MockData.initialHostels.any { it.id == hostel.id }) {
            MockData.initialHostels.add(hostel)
        }
        Result.success(Unit)
    }

    suspend fun updateHostel(hostel: Hostel): Result<Unit> = try {
        firestore.collection("hostels").document(hostel.id).set(hostel).await()
        Result.success(Unit)
    } catch (e: Exception) {
        val idx = MockData.initialHostels.indexOfFirst { it.id == hostel.id }
        if (idx >= 0) MockData.initialHostels[idx] = hostel
        Result.success(Unit)
    }

    suspend fun deleteHostel(hostelId: String): Result<Unit> = try {
        firestore.collection("hostels").document(hostelId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        MockData.initialHostels.removeAll { it.id == hostelId }
        Result.success(Unit)
    }

    // Buildings
    fun getBuildingsFlow(hostelId: String): Flow<List<Building>> = callbackFlow {
        val subscription = firestore.collection("buildings")
            .whereEqualTo("hostelId", hostelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(MockData.initialBuildings.filter { it.hostelId == hostelId })
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Building::class.java) } ?: emptyList()
                trySend(if (list.isNotEmpty()) list else MockData.initialBuildings.filter { it.hostelId == hostelId })
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addBuilding(building: Building): Result<Unit> = try {
        firestore.collection("buildings").document(building.id).set(building).await()
        Result.success(Unit)
    } catch (e: Exception) {
        if (!MockData.initialBuildings.any { it.id == building.id }) {
            MockData.initialBuildings.add(building)
        }
        Result.success(Unit)
    }

    // Floors
    fun getFloorsFlow(buildingId: String): Flow<List<Floor>> = callbackFlow {
        val subscription = firestore.collection("floors")
            .whereEqualTo("buildingId", buildingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(MockData.initialFloors.filter { it.buildingId == buildingId })
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Floor::class.java) } ?: emptyList()
                trySend(if (list.isNotEmpty()) list else MockData.initialFloors.filter { it.buildingId == buildingId })
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addFloor(floor: Floor): Result<Unit> = try {
        firestore.collection("floors").document(floor.id).set(floor).await()
        Result.success(Unit)
    } catch (e: Exception) {
        if (!MockData.initialFloors.any { it.id == floor.id }) {
            MockData.initialFloors.add(floor)
        }
        Result.success(Unit)
    }

    // Beds
    fun getBedsFlow(roomId: String): Flow<List<Bed>> = callbackFlow {
        val subscription = firestore.collection("beds")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(MockData.initialBeds.filter { it.roomId == roomId })
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Bed::class.java) } ?: emptyList()
                trySend(if (list.isNotEmpty()) list else MockData.initialBeds.filter { it.roomId == roomId })
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addBed(bed: Bed): Result<Unit> = try {
        firestore.collection("beds").document(bed.id).set(bed).await()
        Result.success(Unit)
    } catch (e: Exception) {
        if (!MockData.initialBeds.any { it.id == bed.id }) {
            MockData.initialBeds.add(bed)
        }
        Result.success(Unit)
    }
}
