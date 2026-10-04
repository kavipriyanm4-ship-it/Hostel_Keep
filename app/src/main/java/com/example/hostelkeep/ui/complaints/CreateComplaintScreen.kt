package com.example.hostelkeep.ui.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.ComplaintViewModel

@Composable
fun CreateComplaintScreen(complaintViewModel: ComplaintViewModel, onBack: () -> Unit) {
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "Report Issue / Complaint", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = category, onValueChange = { category = it }, label = "Category (Electrical/Plumbing/Internet/...)")
            AppTextField(value = description, onValueChange = { description = it }, label = "Description")
            AppTextField(value = priority, onValueChange = { priority = it }, label = "Priority (Low/Medium/High)")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Submit Complaint",
                onClick = {
                    complaintViewModel.addComplaint("s1", "Arun Kumar", "101", category, description, priority)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
