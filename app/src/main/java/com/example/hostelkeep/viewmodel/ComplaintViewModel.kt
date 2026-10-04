package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.ComplaintRepository
import com.example.hostelkeep.model.Complaint
import com.example.hostelkeep.model.ComplaintStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ComplaintViewModel(
    private val complaintRepository: ComplaintRepository = ComplaintRepository()
) : ViewModel() {
    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    init {
        viewModelScope.launch {
            complaintRepository.getComplaintsFlow().collect { list ->
                _complaints.value = list
            }
        }
    }

    fun addComplaint(studentId: String, studentName: String, roomNo: String, category: String, description: String, priority: String) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val newC = Complaint(
                id = "c_${System.currentTimeMillis()}",
                studentId = studentId,
                studentName = studentName,
                roomNo = roomNo,
                category = category,
                description = description,
                status = ComplaintStatus.OPEN,
                date = df.format(Date()),
                priority = priority
            )
            complaintRepository.addComplaint(newC)
        }
    }

    fun updateStatus(complaintId: String, status: ComplaintStatus) {
        viewModelScope.launch {
            complaintRepository.updateStatus(complaintId, status)
        }
    }
}
