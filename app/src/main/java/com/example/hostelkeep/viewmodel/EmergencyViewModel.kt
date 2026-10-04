package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.EmergencyRepository
import com.example.hostelkeep.model.Emergency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EmergencyViewModel(
    private val emergencyRepository: EmergencyRepository = EmergencyRepository()
) : ViewModel() {
    private val _emergencies = MutableStateFlow<List<Emergency>>(emptyList())
    val emergencies: StateFlow<List<Emergency>> = _emergencies.asStateFlow()

    init {
        viewModelScope.launch {
            emergencyRepository.getEmergenciesFlow().collect { list ->
                _emergencies.value = list
            }
        }
    }

    fun triggerSOS(studentName: String, rollNo: String, roomNo: String, type: String) {
        viewModelScope.launch {
            val timeDf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val newE = Emergency(
                id = "e_${System.currentTimeMillis()}",
                studentName = studentName,
                rollNo = rollNo,
                roomNo = roomNo,
                emergencyType = type,
                time = timeDf.format(Date()),
                status = "Active"
            )
            emergencyRepository.triggerSOS(newE)
        }
    }

    fun resolveEmergency(id: String) {
        viewModelScope.launch {
            emergencyRepository.resolveEmergency(id)
        }
    }
}
