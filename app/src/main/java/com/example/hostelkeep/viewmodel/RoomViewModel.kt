package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.RoomRepository
import com.example.hostelkeep.model.Bed
import com.example.hostelkeep.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    private val roomRepository: RoomRepository = RoomRepository()
) : ViewModel() {
    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _selectedRoom = MutableStateFlow<Room?>(null)
    val selectedRoom: StateFlow<Room?> = _selectedRoom.asStateFlow()

    private val _beds = MutableStateFlow<List<Bed>>(emptyList())
    val beds: StateFlow<List<Bed>> = _beds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                roomRepository.getRoomsFlow().collect { list ->
                    _rooms.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load rooms"
                _isLoading.value = false
            }
        }
    }

    fun loadRoomDetails(roomId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val room = roomRepository.getRoomById(roomId)
            _selectedRoom.value = room
            if (room != null) {
                loadBeds(roomId)
            }
            _isLoading.value = false
        }
    }

    fun loadBeds(roomId: String) {
        viewModelScope.launch {
            roomRepository.getBedsForRoom(roomId).collect { bedList ->
                _beds.value = bedList
            }
        }
    }

    fun addRoom(room: Room, numBeds: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = roomRepository.addRoom(room, numBeds)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "Room added successfully!"
                loadRooms()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Failed to add room"
            }
        }
    }

    fun updateRoom(room: Room, newCapacity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = roomRepository.updateRoom(room, newCapacity)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "Room updated successfully!"
                loadRooms()
                loadRoomDetails(room.id)
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Failed to update room"
            }
        }
    }

    fun deleteRoom(roomId: String, isAdmin: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = roomRepository.deleteRoom(roomId, isAdmin)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "Room deleted successfully!"
                loadRooms()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Failed to delete room"
            }
        }
    }

    fun updateBed(bed: Bed) {
        viewModelScope.launch {
            val result = roomRepository.updateBed(bed)
            result.onSuccess {
                _successMessage.value = "Bed updated successfully!"
                loadBeds(bed.roomId)
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Failed to update bed"
            }
        }
    }

    fun allocateBed(studentId: String, hostelId: String, roomId: String, bedId: String, allocatedBy: String) {
        viewModelScope.launch {
            val result = roomRepository.allocateBed(studentId, hostelId, roomId, bedId, allocatedBy)
            result.onSuccess {
                _successMessage.value = "Bed allocated successfully!"
                loadRooms()
                loadBeds(roomId)
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage ?: "Failed to allocate bed"
            }
        }
    }

    fun vacateBed(allocationId: String, studentId: String, roomId: String, bedId: String, vacatedBy: String = "Admin") {
        viewModelScope.launch {
            val result = roomRepository.vacateBed(allocationId, studentId, roomId, bedId, vacatedBy)
            result.onSuccess {
                _successMessage.value = "Bed vacated successfully!"
                loadRooms()
                loadBeds(roomId)
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage ?: "Failed to vacate bed"
            }
        }
    }

    fun allocateRoom(roomId: String, studentName: String) {
        viewModelScope.launch {
            val room = _rooms.value.find { it.id == roomId }
            if (room != null && room.occupiedBeds < room.capacity) {
                _successMessage.value = "Room allocated to $studentName"
            } else {
                _errorMessage.value = "Room capacity is full"
            }
        }
    }

    fun vacateRoom(roomId: String, studentName: String) {
        viewModelScope.launch {
            _successMessage.value = "Room vacated for $studentName"
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
