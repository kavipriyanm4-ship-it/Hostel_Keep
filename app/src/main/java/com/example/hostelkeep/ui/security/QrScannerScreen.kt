package com.example.hostelkeep.ui.security

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTextField
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.model.User
import com.example.hostelkeep.viewmodel.OutpassViewModel

@Composable
fun QrScannerScreen(
    currentUser: User?,
    outpassViewModel: OutpassViewModel, 
    onBack: () -> Unit
) {
    var scanInput by remember { mutableStateOf("") }
    var scanType by remember { mutableStateOf("EXIT") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessMessage by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Scaffold(topBar = { AppTopBar(title = "QR Outpass Scanner", onMenuClick = onBack) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Gate Scanner", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                FilterChip(
                    selected = scanType == "EXIT",
                    onClick = { scanType = "EXIT" },
                    label = { Text("Exit") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = scanType == "ENTRY",
                    onClick = { scanType = "ENTRY" },
                    label = { Text("Entry") }
                )
            }
            
            Spacer(Modifier.height(24.dp))
            AppTextField(
                value = scanInput, 
                onValueChange = { scanInput = it }, 
                label = "Enter QR Token String"
            )
            
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = "Verify QR",
                onClick = {
                    if (currentUser == null) {
                        Toast.makeText(context, "Authentication required", Toast.LENGTH_SHORT).show()
                        return@AppButton
                    }
                    if (scanInput.isBlank()) {
                        Toast.makeText(context, "Please enter a token", Toast.LENGTH_SHORT).show()
                        return@AppButton
                    }
                    
                    outpassViewModel.scanQrCode(scanInput, currentUser.id, scanType) { success, error ->
                        isSuccessMessage = success
                        resultMessage = if (success) {
                            "Verification Successful! Student " + (if (scanType == "EXIT") "Checked Out." else "Returned.")
                        } else {
                            "Failed: ${error ?: "Unknown error"}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (resultMessage != null) {
                Spacer(Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccessMessage) MaterialTheme.colorScheme.primaryContainer 
                                         else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = resultMessage!!, 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = if (isSuccessMessage) MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
