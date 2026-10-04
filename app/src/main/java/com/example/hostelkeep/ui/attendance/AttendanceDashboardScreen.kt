package com.example.hostelkeep.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.AttendanceStatus
import com.example.hostelkeep.viewmodel.AttendanceViewModel
import com.example.hostelkeep.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDashboardScreen(attendanceViewModel: AttendanceViewModel, authViewModel: AuthViewModel, onBack: () -> Unit) {
    val selectedDate by attendanceViewModel.selectedDate.collectAsState()
    val searchedStudent by attendanceViewModel.searchedStudent.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf<String?>(null) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf(AttendanceStatus.PRESENT) }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())
    val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 86400000))

    LaunchedEffect(Unit) {
        if (selectedDate.isEmpty()) {
            attendanceViewModel.setDate(today)
        }
        attendanceViewModel.clearSearchedStudent()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Attendance Management", onMenuClick = onBack) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            // Date Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selectedDate == yesterday,
                    onClick = { attendanceViewModel.setDate(yesterday) },
                    label = { Text("Yesterday") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = selectedDate == today,
                    onClick = { attendanceViewModel.setDate(today) },
                    label = { Text("Today") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Search by Roll Number
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it 
                    searchError = null
                    saveMessage = null
                },
                label = { Text("Search by Roll Number") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) {
                            saveMessage = null
                            attendanceViewModel.searchStudentByRollNumber(searchQuery.trim())
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (searchError != null) {
                Text(searchError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (saveMessage != null) {
                Text(saveMessage!!, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Display Searched Student
            if (searchedStudent != null) {
                val student = searchedStudent!!
                
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(student.name, style = MaterialTheme.typography.titleLarge)
                        Text("Roll: ${student.rollNo} • Hostel: ${student.hostelId}", style = MaterialTheme.typography.bodyMedium)
                        Text("Room: ${student.roomNo} • Bed: ${student.bedNo}", style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Validation Error Check
                        if (currentUser?.hostelName != null && currentUser?.hostelName != "" && student.hostelId != currentUser?.hostelName) {
                            Text("This student is not assigned to your hostel.", color = MaterialTheme.colorScheme.error)
                        } else {
                            // Attendance Options
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                val options = AttendanceStatus.values()
                                options.forEachIndexed { index, status ->
                                    SegmentedButton(
                                        selected = selectedStatus == status,
                                        onClick = { selectedStatus = status },
                                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                                    ) {
                                        Text(status.name, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    attendanceViewModel.markSingleAttendance(
                                        student = student,
                                        status = selectedStatus,
                                        markedBy = currentUser?.name ?: "Admin",
                                        markedByRole = currentUser?.role?.name ?: "ADMIN",
                                        onSuccess = {
                                            saveMessage = "Attendance marked successfully."
                                            attendanceViewModel.clearSearchedStudent()
                                            searchQuery = ""
                                        },
                                        onError = { searchError = it }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SAVE ATTENDANCE")
                            }
                        }
                    }
                }
            } else if (searchQuery.isNotBlank() && searchError == null && saveMessage == null) {
                // If it's a fresh search that returns nothing? Actually, we might need to handle empty states.
            }
        }
    }
}
