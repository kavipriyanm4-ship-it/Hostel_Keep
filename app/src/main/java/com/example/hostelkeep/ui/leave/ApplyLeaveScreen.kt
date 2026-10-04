package com.example.hostelkeep.ui.leave

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.LeaveType
import com.example.hostelkeep.viewmodel.AuthViewModel
import com.example.hostelkeep.viewmodel.LeaveViewModel

@Composable
fun ApplyLeaveScreen(leaveViewModel: LeaveViewModel, authViewModel: AuthViewModel, onBack: () -> Unit) {
    val currentUser by authViewModel.currentUser.collectAsState()

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(LeaveType.HOME) }

    Scaffold(topBar = { AppTopBar(title = "Apply Leave", onMenuClick = onBack) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Leave Type", style = MaterialTheme.typography.labelLarge)
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedType.name)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                LeaveType.values().forEach { type ->
                    DropdownMenuItem(text = { Text(type.name) }, onClick = { selectedType = type; expanded = false })
                }
            }

            AppTextField(value = fromDate, onValueChange = { fromDate = it }, label = "From Date (YYYY-MM-DD)")
            AppTextField(value = toDate, onValueChange = { toDate = it }, label = "To Date (YYYY-MM-DD)")
            AppTextField(value = destination, onValueChange = { destination = it }, label = "Destination")
            AppTextField(value = contactNumber, onValueChange = { contactNumber = it }, label = "Emergency Contact Number")
            AppTextField(value = reason, onValueChange = { reason = it }, label = "Reason for Leave")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Submit Leave Application",
                onClick = {
                    currentUser?.let { user ->
                        leaveViewModel.applyLeave(
                            studentId = user.id,
                            studentName = user.name,
                            rollNo = user.email, // using email as fallback if rollNo isn't readily available in User
                            hostelId = user.hostelName,
                            roomId = user.roomNumber,
                            leaveType = selectedType,
                            from = fromDate,
                            to = toDate,
                            reason = reason,
                            destination = destination,
                            contactNumber = contactNumber
                        )
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
