package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.AttendanceRepository
import com.example.hostelkeep.data.repository.StudentRepository
import com.example.hostelkeep.model.Attendance
import com.example.hostelkeep.model.AttendanceStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository = AttendanceRepository(),
    private val studentRepository: StudentRepository = StudentRepository()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedHostelId = MutableStateFlow("")
    val selectedHostelId: StateFlow<String> = _selectedHostelId.asStateFlow()

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList.asStateFlow()

    private val _studentHistory = MutableStateFlow<List<Attendance>>(emptyList())
    val studentHistory: StateFlow<List<Attendance>> = _studentHistory.asStateFlow()

    private val _searchedStudent = MutableStateFlow<com.example.hostelkeep.model.Student?>(null)
    val searchedStudent: StateFlow<com.example.hostelkeep.model.Student?> = _searchedStudent.asStateFlow()

    data class AttendanceStats(
        val present: Int = 0,
        val absent: Int = 0,
        val late: Int = 0,
        val excused: Int = 0,
        val total: Int = 0,
        val percentage: Float = 0f
    )

    val attendanceStats: StateFlow<AttendanceStats> = _studentHistory.map { history ->
        if (history.isEmpty()) return@map AttendanceStats()
        val total = history.size
        val present = history.count { it.status == AttendanceStatus.PRESENT }
        val absent = history.count { it.status == AttendanceStatus.ABSENT }
        val late = history.count { it.status == AttendanceStatus.LATE }
        val excused = history.count { it.status == AttendanceStatus.EXCUSED }
        val percentage = ((present + excused).toFloat() / total.toFloat()) * 100f
        AttendanceStats(present, absent, late, excused, total, percentage)
    }.stateIn(viewModelScope, SharingStarted.Lazily, AttendanceStats())

    val attendancePercentage: StateFlow<Float> = attendanceStats.map { it.percentage }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    
    fun loadStudentsForAttendance(hostelId: String, date: String) {
        _selectedHostelId.value = hostelId
        _selectedDate.value = date
        viewModelScope.launch {
            attendanceRepository.getAttendanceForHostel(hostelId, date).collectLatest { existingAttendance ->
                val studentsResult = studentRepository.getStudentsByHostel(hostelId)
                if (studentsResult.isSuccess) {
                    val students = studentsResult.getOrDefault(emptyList())
                    val mergedList = students.map { student ->
                        val existing = existingAttendance.find { it.studentId == student.id }
                        existing ?: Attendance(
                            studentId = student.id,
                            hostelId = hostelId,
                            roomId = student.roomId,
                            bedId = student.bedId,
                            studentName = student.name,
                            rollNo = student.rollNo,
                            date = date,
                            status = AttendanceStatus.PRESENT
                        )
                    }
                    _attendanceList.value = mergedList
                }
            }
        }
    }

    fun updateAttendanceStatus(studentId: String, status: AttendanceStatus) {
        _attendanceList.value = _attendanceList.value.map {
            if (it.studentId == studentId) it.copy(status = status) else it
        }
    }

    fun saveBatchAttendance(markedBy: String, markedByRole: String) {
        val records = _attendanceList.value.map {
            it.copy(
                markedBy = markedBy,
                markedByRole = markedByRole,
                markedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                updatedBy = markedBy
            )
        }
        viewModelScope.launch {
            attendanceRepository.markBatchAttendance(records)
        }
    }

    fun loadStudentHistory(studentId: String) {
        viewModelScope.launch {
            attendanceRepository.getStudentAttendanceHistory(studentId).collectLatest { history ->
                _studentHistory.value = history
            }
        }
    }
    
    fun setDate(date: String) {
        _selectedDate.value = date
        if (_selectedHostelId.value.isNotEmpty()) {
            loadStudentsForAttendance(_selectedHostelId.value, date)
        }
    }

    fun searchStudentByRollNumber(rollNo: String) {
        viewModelScope.launch {
            val student = attendanceRepository.searchStudentByRollNo(rollNo)
            _searchedStudent.value = student
        }
    }

    fun clearSearchedStudent() {
        _searchedStudent.value = null
    }

    fun markSingleAttendance(
        student: com.example.hostelkeep.model.Student,
        status: AttendanceStatus,
        markedBy: String,
        markedByRole: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = attendanceRepository.markSingleAttendance(
                student = student,
                date = _selectedDate.value,
                status = status,
                markedBy = markedBy,
                markedByRole = markedByRole
            )
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Failed to mark attendance")
            }
        }
    }
}
