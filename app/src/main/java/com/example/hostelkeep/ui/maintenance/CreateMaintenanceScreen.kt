package com.example.hostelkeep.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.MaintenanceViewModel

@Composable
fun CreateMaintenanceScreen(maintenanceViewModel: MaintenanceViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Scaffold(topBar = { AppTopBar(title = "New Maintenance Task", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = title, onValueChange = { title = it }, label = "Task Title")
            AppTextField(value = description, onValueChange = { description = it }, label = "Description")
            AppTextField(value = location, onValueChange = { location = it }, label = "Location")
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Create Task",
                onClick = {
                    if (title.isNotEmpty()) {
                        maintenanceViewModel.addMaintenance(title, description, location, category)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
