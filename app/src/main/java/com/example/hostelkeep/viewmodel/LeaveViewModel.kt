package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.LeaveRepository
import com.example.hostelkeep.model.Leave
import com.example.hostelkeep.model.LeaveType
import com.example.hostelkeep.model.RequestStatus
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaveViewModel(
    private val leaveRepository: LeaveRepository = LeaveRepository()
) : ViewModel() {
    private val _leaves = MutableStateFlow<List<Leave>>(emptyList())
    val leaves: StateFlow<List<Leave>> = _leaves.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadLeaves(currentUser: User?) {
        if (currentUser == null) return
        viewModelScope.launch {
            _isLoading.value = true
            val flow = when (currentUser.role) {
                UserRole.ADMIN -> leaveRepository.getAllLeavesAdmin()
                UserRole.WARDEN -> leaveRepository.getLeavesForHostel(currentUser.hostelName) // Warning: use hostelId properly in production
                UserRole.STUDENT, UserRole.PARENT -> leaveRepository.getLeavesForStudent(currentUser.id)
                UserRole.SECURITY -> leaveRepository.getLeavesForHostel(currentUser.hostelName)
            }

            flow.collect { list ->
                _leaves.value = list
                _isLoading.value = false
            }
        }
    }

    fun applyLeave(
        studentId: String,
        studentName: String,
        rollNo: String,
        hostelId: String,
        roomId: String,
        leaveType: LeaveType,
        from: String,
        to: String,
        reason: String,
        destination: String,
        contactNumber: String
    ) {
        viewModelScope.launch {
            val newLeave = Leave(
                studentId = studentId,
                hostelId = hostelId,
                roomId = roomId,
                studentName = studentName,
                rollNo = rollNo,
                leaveType = leaveType,
                fromDate = from,
                toDate = to,
                reason = reason,
                destination = destination,
                contactNumber = contactNumber,
                status = RequestStatus.PENDING,
                requestedAt = System.currentTimeMillis()
            )
            leaveRepository.applyLeave(newLeave)
        }
    }

    fun updateStatus(leaveId: String, status: RequestStatus, reviewedBy: String, comment: String = "") {
        viewModelScope.launch {
            leaveRepository.updateStatus(leaveId, status, reviewedBy, comment)
        }
    }

    fun cancelLeave(leaveId: String) {
        viewModelScope.launch {
            leaveRepository.cancelLeave(leaveId)
        }
    }
}
