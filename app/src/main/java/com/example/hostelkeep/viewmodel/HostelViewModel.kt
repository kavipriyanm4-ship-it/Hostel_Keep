package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.HostelRepository
import com.example.hostelkeep.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HostelViewModel(
    private val hostelRepository: HostelRepository = HostelRepository()
) : ViewModel() {
    private val _hostels = MutableStateFlow<List<Hostel>>(emptyList())
    val hostels: StateFlow<List<Hostel>> = _hostels.asStateFlow()

    private val _buildings = MutableStateFlow<List<Building>>(emptyList())
    val buildings: StateFlow<List<Building>> = _buildings.asStateFlow()

    private val _floors = MutableStateFlow<List<Floor>>(emptyList())
    val floors: StateFlow<List<Floor>> = _floors.asStateFlow()

    private val _beds = MutableStateFlow<List<Bed>>(emptyList())
    val beds: StateFlow<List<Bed>> = _beds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        viewModelScope.launch {
            hostelRepository.getHostelsFlow().collect { list ->
                _hostels.value = list
            }
        }
    }

    fun loadBuildings(hostelId: String) {
        viewModelScope.launch {
            hostelRepository.getBuildingsFlow(hostelId).collect { list ->
                _buildings.value = list
            }
        }
    }

    fun loadFloors(buildingId: String) {
        viewModelScope.launch {
            hostelRepository.getFloorsFlow(buildingId).collect { list ->
                _floors.value = list
            }
        }
    }

    fun loadBeds(roomId: String) {
        viewModelScope.launch {
            hostelRepository.getBedsFlow(roomId).collect { list ->
                _beds.value = list
            }
        }
    }

    fun addHostel(hostel: Hostel) {
        viewModelScope.launch {
            val result = hostelRepository.addHostel(hostel)
            result.onSuccess { _successMessage.value = "Hostel added successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to add hostel" }
        }
    }

    fun addBuilding(building: Building) {
        viewModelScope.launch {
            val result = hostelRepository.addBuilding(building)
            result.onSuccess { _successMessage.value = "Building added successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to add building" }
        }
    }

    fun addFloor(floor: Floor) {
        viewModelScope.launch {
            val result = hostelRepository.addFloor(floor)
            result.onSuccess { _successMessage.value = "Floor added successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to add floor" }
        }
    }

    fun addBed(bed: Bed) {
        viewModelScope.launch {
            val result = hostelRepository.addBed(bed)
            result.onSuccess { _successMessage.value = "Bed added successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to add bed" }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
