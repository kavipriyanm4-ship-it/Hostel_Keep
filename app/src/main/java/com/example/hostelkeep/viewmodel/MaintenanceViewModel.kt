package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.MaintenanceRepository
import com.example.hostelkeep.model.ComplaintStatus
import com.example.hostelkeep.model.Maintenance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MaintenanceViewModel(
    private val maintenanceRepository: MaintenanceRepository = MaintenanceRepository()
) : ViewModel() {
    private val _maintenanceList = MutableStateFlow<List<Maintenance>>(emptyList())
    val maintenanceList: StateFlow<List<Maintenance>> = _maintenanceList.asStateFlow()

    init {
        viewModelScope.launch {
            maintenanceRepository.getMaintenanceFlow().collect { list ->
                _maintenanceList.value = list
            }
        }
    }

    fun addMaintenance(title: String, description: String, location: String, category: String) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val newM = Maintenance(
                id = "m_${System.currentTimeMillis()}",
                title = title,
                description = description,
                location = location,
                category = category,
                status = ComplaintStatus.OPEN,
                reportedDate = df.format(Date()),
                assignedTo = "Unassigned"
            )
            maintenanceRepository.addMaintenance(newM)
        }
    }

    fun updateStatus(id: String, status: ComplaintStatus) {
        viewModelScope.launch {
            maintenanceRepository.updateStatus(id, status)
        }
    }
}
