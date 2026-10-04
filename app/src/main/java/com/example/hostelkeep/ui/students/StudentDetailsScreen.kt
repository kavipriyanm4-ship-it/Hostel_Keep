package com.example.hostelkeep.ui.students

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.ProfileHeader
import com.example.hostelkeep.viewmodel.StudentViewModel

@Composable
fun StudentDetailsScreen(studentId: String, studentViewModel: StudentViewModel, onBack: () -> Unit) {
    val students by studentViewModel.students.collectAsState()
    val student = students.find { it.id == studentId } ?: students.firstOrNull()

    Scaffold(topBar = { AppTopBar(title = "Student Details", onMenuClick = onBack) }) { padding ->
        if (student != null) {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                ProfileHeader(name = student.name, subtitle = "Roll: ${student.rollNo} • Dept: ${student.department}", email = student.email)
                Spacer(Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Room: ${student.roomNo} (${student.bedNo})")
                        Text("Phone: ${student.phone}")
                        Text("Parent: ${student.parentName} (${student.parentPhone})")
                        Text("Blood Group: ${student.bloodGroup}")
                        Text("Status: ${student.status}")
                    }
                }
            }
        }
    }
}
