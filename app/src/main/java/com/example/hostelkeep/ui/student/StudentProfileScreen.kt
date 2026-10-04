package com.example.hostelkeep.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.components.ProfileHeader
import com.example.hostelkeep.viewmodel.AuthViewModel
import com.example.hostelkeep.viewmodel.StudentViewModel

@Composable
fun StudentProfileScreen(
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel = viewModel(),
    onBack: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val student by studentViewModel.student.collectAsState()
    val hostel by studentViewModel.hostel.collectAsState()
    val room by studentViewModel.room.collectAsState()
    val bed by studentViewModel.bed.collectAsState()
    val loading by studentViewModel.loading.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    // Editable fields (permitted fields)
    var editName by remember(student) { mutableStateOf(student?.name ?: currentUser?.name ?: "") }
    var editPhone by remember(student) { mutableStateOf(student?.phone ?: currentUser?.phone ?: "") }
    var editParentName by remember(student) { mutableStateOf(student?.parentName ?: "") }
    var editParentPhone by remember(student) { mutableStateOf(student?.parentPhone ?: "") }
    var editBloodGroup by remember(student) { mutableStateOf(student?.bloodGroup ?: "") }
    var editAddress by remember(student) { mutableStateOf(student?.address ?: "") }
    var editProfileImageUrl by remember(student) { mutableStateOf(student?.profileImageUrl ?: "") }

    val studentId = student?.id.takeIf { !it.isNullOrEmpty() } ?: student?.uid.takeIf { !it.isNullOrEmpty() } ?: currentUser?.id ?: "s1"

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Permitted Fields", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentName,
                        onValueChange = { editParentName = it },
                        label = { Text("Parent / Guardian Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentPhone,
                        onValueChange = { editParentPhone = it },
                        label = { Text("Parent Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBloodGroup,
                        onValueChange = { editBloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProfileImageUrl,
                        onValueChange = { editProfileImageUrl = it },
                        label = { Text("Profile Image URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Restricted Fields (Locked)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    OutlinedTextField(
                        value = student?.rollNo ?: "N/A",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Roll Number (Locked)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = student?.department ?: "N/A",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Department (Locked)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = student?.email ?: currentUser?.email ?: "",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Email (Locked)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updates = mapOf(
                            "name" to editName,
                            "phone" to editPhone,
                            "parentName" to editParentName,
                            "parentPhone" to editParentPhone,
                            "bloodGroup" to editBloodGroup,
                            "address" to editAddress,
                            "profileImageUrl" to editProfileImageUrl
                        )
                        studentViewModel.updateProfile(studentId, updates)
                        showEditDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Student Profile",
                onMenuClick = onBack,
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val name = student?.name.takeIf { !it.isNullOrEmpty() } ?: currentUser?.name ?: "Student"
            val rollNo = student?.rollNo.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val email = student?.email.takeIf { !it.isNullOrEmpty() } ?: currentUser?.email ?: ""
            val phone = student?.phone.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val dept = student?.department.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val course = student?.course.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val year = student?.year?.toString() ?: "N/A"
            val parentName = student?.parentName.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val parentPhone = student?.parentPhone.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val bloodGroup = student?.bloodGroup.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val address = student?.address.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val status = student?.status.takeIf { !it.isNullOrEmpty() } ?: "ACTIVE"

            val hostelName = hostel?.name ?: student?.hostelId.takeIf { !it.isNullOrEmpty() } ?: "Not Allocated"
            val roomNo = room?.roomNo?.ifEmpty { room?.roomNumber } ?: student?.roomNo.takeIf { !it.isNullOrEmpty() } ?: "N/A"
            val bedNo = bed?.bedNo?.ifEmpty { bed?.bedNumber } ?: student?.bedNo.takeIf { !it.isNullOrEmpty() } ?: "N/A"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(
                    name = name,
                    subtitle = "$dept • Year $year ($course)",
                    email = email
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Academic & Personal Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Divider()
                        ProfileDetailRow("Roll Number", rollNo)
                        ProfileDetailRow("Email", email)
                        ProfileDetailRow("Phone", phone)
                        ProfileDetailRow("Department", dept)
                        ProfileDetailRow("Course", course)
                        ProfileDetailRow("Year", year)
                        ProfileDetailRow("Blood Group", bloodGroup)
                        ProfileDetailRow("Status", status)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Hostel Allocation", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Divider()
                        ProfileDetailRow("Hostel", hostelName)
                        ProfileDetailRow("Room No", roomNo)
                        ProfileDetailRow("Bed No", bedNo)
                        ProfileDetailRow("Address", address)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Parent / Guardian Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Divider()
                        ProfileDetailRow("Parent Name", parentName)
                        ProfileDetailRow("Parent Phone", parentPhone)
                    }
                }

                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Profile")
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
