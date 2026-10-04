package com.example.hostelkeep.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.StatusChip
import com.example.hostelkeep.model.AttendanceStatus
import com.example.hostelkeep.viewmodel.AttendanceViewModel
import com.example.hostelkeep.viewmodel.AuthViewModel

@Composable
fun StudentAttendanceScreen(attendanceViewModel: AttendanceViewModel, authViewModel: AuthViewModel, onBack: () -> Unit) {
    val history by attendanceViewModel.studentHistory.collectAsState()
    val stats by attendanceViewModel.attendanceStats.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.id?.let {
            attendanceViewModel.loadStudentHistory(it)
        }
    }

    Scaffold(topBar = { AppTopBar(title = "My Attendance", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            progress = { if (stats.total == 0) 0f else stats.percentage / 100f },
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 8.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Attendance", style = MaterialTheme.typography.titleLarge)
                            Text("${String.format(java.util.Locale.getDefault(), "%.1f", stats.percentage)}%", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Present: ${stats.present}")
                        Text("Absent: ${stats.absent}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Late: ${stats.late}")
                        Text("Excused: ${stats.excused}")
                    }
                    Text("Total Marked: ${stats.total}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history.sortedByDescending { it.date }) { att ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(att.date, style = MaterialTheme.typography.bodyLarge)
                            StatusChip(
                                text = att.status.name,
                                color = when (att.status) {
                                    AttendanceStatus.PRESENT -> Color(0xFF4CAF50)
                                    AttendanceStatus.ABSENT -> Color(0xFFF44336)
                                    AttendanceStatus.LATE -> Color(0xFFFF9800)
                                    AttendanceStatus.EXCUSED -> Color(0xFF2196F3)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
