package com.example.hostelkeep.ui.outpass

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.OutpassViewModel
import com.example.hostelkeep.model.User

@Composable
fun ApplyOutpassScreen(
    currentUser: User?,
    outpassViewModel: OutpassViewModel, 
    onBack: () -> Unit
) {
    var destination by remember { mutableStateOf("") }
    var outTime by remember { mutableStateOf("") }
    var inTime by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(topBar = { AppTopBar(title = "Apply Outpass", onMenuClick = onBack) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(value = destination, onValueChange = { destination = it }, label = "Destination")
            AppTextField(value = outTime, onValueChange = { outTime = it }, label = "Out Time (HH:MM)")
            AppTextField(value = inTime, onValueChange = { inTime = it }, label = "In Time (HH:MM)")
            AppTextField(value = reason, onValueChange = { reason = it }, label = "Purpose")
            AppTextField(value = emergencyContact, onValueChange = { emergencyContact = it }, label = "Emergency Contact")
            
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Submit Outpass",
                enabled = !isLoading && destination.isNotBlank() && outTime.isNotBlank() && inTime.isNotBlank() && reason.isNotBlank(),
                onClick = {
                    if (currentUser == null) return@AppButton
                    isLoading = true
                    outpassViewModel.applyOutpass(
                        currentUser, destination, outTime, inTime, reason, emergencyContact
                    ) { success, error ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Outpass applied successfully", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, "Failed: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
