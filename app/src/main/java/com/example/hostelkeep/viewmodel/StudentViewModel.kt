package com.example.hostelkeep.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.data.repository.HostelRepository
import com.example.hostelkeep.data.repository.StorageRepository
import com.example.hostelkeep.data.repository.StudentRepository
import com.example.hostelkeep.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudentViewModel(
    private val studentRepository: StudentRepository = StudentRepository(),
    private val hostelRepository: HostelRepository = HostelRepository(),
    private val storageRepository: StorageRepository = StorageRepository()
) : ViewModel() {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> = _student.asStateFlow()

    private val _hostel = MutableStateFlow<Hostel?>(null)
    val hostel: StateFlow<Hostel?> = _hostel.asStateFlow()

    private val _room = MutableStateFlow<Room?>(null)
    val room: StateFlow<Room?> = _room.asStateFlow()

    private val _bed = MutableStateFlow<Bed?>(null)
    val bed: StateFlow<Bed?> = _bed.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("All")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedYear = MutableStateFlow("All")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val filteredStudents: StateFlow<List<Student>> = combine(
        _students,
        _searchQuery,
        _selectedDepartment,
        _selectedYear
    ) { studentList, query, dept, year ->
        studentList.filter { student ->
            val matchesQuery = query.isEmpty() ||
                    student.name.contains(query, ignoreCase = true) ||
                    student.rollNo.contains(query, ignoreCase = true) ||
                    student.admissionNumber.contains(query, ignoreCase = true)

            val matchesDept = dept == "All" || student.department.equals(dept, ignoreCase = true)
            val matchesYear = year == "All" || student.year.toString() == year

            matchesQuery && matchesDept && matchesYear
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            studentRepository.getStudentsFlow().collect { list ->
                _students.value = list
            }
        }

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            ?: MockData.initialStudents.firstOrNull()?.id ?: "s1"
        loadStudentProfile(currentUid)
    }

    fun loadStudentProfile(uid: String) {
        if (uid.isEmpty()) return
        _loading.value = true
        viewModelScope.launch {
            try {
                studentRepository.getStudentFlow(uid).collect { studentObj ->
                    _student.value = studentObj
                    _loading.value = false
                    if (studentObj != null) {
                        fetchHostelDetails(studentObj.hostelId, studentObj.roomId, studentObj.bedId)
                    }
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.localizedMessage ?: "Failed to load student profile"
            }
        }
    }

    private fun fetchHostelDetails(hostelId: String, roomId: String, bedId: String) {
        viewModelScope.launch {
            try {
                _hostel.value = if (hostelId.isNotEmpty()) hostelRepository.getHostel(hostelId) else null
                _room.value = if (roomId.isNotEmpty()) hostelRepository.getRoom(roomId) else null
                _bed.value = if (bedId.isNotEmpty()) hostelRepository.getBed(bedId) else null
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedDepartment(dept: String) {
        _selectedDepartment.value = dept
    }

    fun setSelectedYear(year: String) {
        _selectedYear.value = year
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            val result = studentRepository.addStudent(student)
            result.onSuccess { _successMessage.value = "Student added successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to add student" }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            val result = studentRepository.updateStudent(student)
            result.onSuccess { _successMessage.value = "Student updated successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to update student" }
        }
    }

    fun updateProfile(uid: String, updates: Map<String, Any>) {
        _loading.value = true
        viewModelScope.launch {
            val result = studentRepository.updateStudentProfile(uid, updates)
            _loading.value = false
            result.onSuccess {
                _successMessage.value = "Profile updated successfully"
                loadStudentProfile(uid)
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Failed to update profile"
                _error.value = it.localizedMessage
            }
        }
    }

    fun uploadProfileImage(uid: String, uri: Uri) {
        _loading.value = true
        viewModelScope.launch {
            val result = storageRepository.uploadProfileImage(uid, uri)
            result.onSuccess { downloadUrl ->
                updateProfile(uid, mapOf("profileImageUrl" to downloadUrl))
            }.onFailure {
                _loading.value = false
                _errorMessage.value = it.localizedMessage ?: "Failed to upload image"
                _error.value = it.localizedMessage
            }
        }
    }

    fun uploadProfileImage(uid: String, bytes: ByteArray) {
        _loading.value = true
        viewModelScope.launch {
            val result = storageRepository.uploadProfileImage(uid, bytes)
            result.onSuccess { downloadUrl ->
                updateProfile(uid, mapOf("profileImageUrl" to downloadUrl))
            }.onFailure {
                _loading.value = false
                _errorMessage.value = it.localizedMessage ?: "Failed to upload image"
                _error.value = it.localizedMessage
            }
        }
    }

    fun deleteStudent(id: String) {
        viewModelScope.launch {
            val result = studentRepository.deleteStudent(id)
            result.onSuccess { _successMessage.value = "Student deleted successfully" }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Failed to delete student" }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
        _error.value = null
    }
}
