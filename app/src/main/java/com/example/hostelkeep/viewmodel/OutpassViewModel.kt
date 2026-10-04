package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.OutpassRepository
import com.example.hostelkeep.model.Outpass
import com.example.hostelkeep.model.RequestStatus
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class OutpassViewModel(
    private val outpassRepository: OutpassRepository = OutpassRepository()
) : ViewModel() {
    private val _outpasses = MutableStateFlow<List<Outpass>>(emptyList())
    val outpasses: StateFlow<List<Outpass>> = _outpasses.asStateFlow()

    private var currentJob: Job? = null

    fun loadData(user: User?) {
        if (user == null) {
            _outpasses.value = emptyList()
            return
        }
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val flow = when (user.role) {
                UserRole.STUDENT, UserRole.PARENT -> outpassRepository.getOutpassesForStudent(user.id)
                UserRole.WARDEN, UserRole.SECURITY -> outpassRepository.getOutpassesForHostel("hostel_1") // Or user.hostelId if available, assuming default
                UserRole.ADMIN -> outpassRepository.getAllOutpassesAdmin()
            }
            flow.collect { list ->
                _outpasses.value = list
            }
        }
    }

    fun applyOutpass(user: User, destination: String, outTime: String, inTime: String, reason: String, emergencyContact: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val qrToken = UUID.randomUUID().toString()
            val newOp = Outpass(
                studentId = user.id,
                studentName = user.name,
                rollNo = user.email.substringBefore("@"), // Fallback if rollNo is not in User
                hostelId = "hostel_1",
                destination = destination,
                outTime = outTime,
                inTime = inTime,
                reason = reason,
                emergencyContact = emergencyContact,
                qrCodeData = qrToken,
                status = RequestStatus.PENDING
            )
            val result = outpassRepository.applyOutpass(newOp)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }

    fun updateStatus(outpassId: String, status: RequestStatus, verifiedBy: String) {
        viewModelScope.launch {
            outpassRepository.updateStatus(outpassId, status, verifiedBy)
        }
    }

    fun scanQrCode(qrData: String, securityId: String, scanType: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = outpassRepository.scanQrCode(qrData, securityId, scanType)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }
}
