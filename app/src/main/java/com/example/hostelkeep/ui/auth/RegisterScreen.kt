package com.example.hostelkeep.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hostelkeep.components.AppButton
import com.example.hostelkeep.components.AppTopBar
import com.example.hostelkeep.viewmodel.AuthState
import com.example.hostelkeep.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var rollNoerror by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var showVerificationDialog by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                showVerificationDialog = true
            }
            is AuthState.Error -> {
                generalError = (authState as AuthState.Error).message
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Student Registration", onMenuClick = onBack)
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Create Student Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Role: Student (Locked)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(24.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; nameError = null; generalError = null },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    isError = nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (nameError != null) {
                    Text(nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null; generalError = null },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (emailError != null) {
                    Text(emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Roll Number
                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it; rollNoerror = null; generalError = null },
                    label = { Text("Roll Number") },
                    leadingIcon = { Icon(Icons.Default.Badge, null) },
                    isError = rollNoerror != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (rollNoerror != null) {
                    Text(rollNoerror!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = null; generalError = null },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    isError = phoneError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (phoneError != null) {
                    Text(phoneError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null; generalError = null },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (passwordError != null) {
                    Text(passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(16.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = null; generalError = null },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (confirmPasswordError != null) {
                    Text(confirmPasswordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }

                if (generalError != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(generalError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(32.dp))

                AppButton(
                    text = if (authState is AuthState.Loading) "Creating Account..." else "Create Account",
                    onClick = {
                        var hasError = false
                        if (fullName.isBlank()) { nameError = "Full name is required"; hasError = true }
                        if (email.isBlank() || !email.contains("@")) { emailError = "Valid email is required"; hasError = true }
                        if (rollNo.isBlank()) { rollNoerror = "Roll number is required"; hasError = true }
                        if (phone.isBlank()) { phoneError = "Phone number is required"; hasError = true }
                        if (password.length < 6) { passwordError = "Password must be at least 6 characters"; hasError = true }
                        if (password != confirmPassword) { confirmPasswordError = "Passwords do not match"; hasError = true }

                        if (!hasError) {
                            authViewModel.registerStudent(fullName.trim(), email.trim(), rollNo.trim(), phone.trim(), password)
                        }
                    },
                    enabled = authState !is AuthState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showVerificationDialog) {
            AlertDialog(
                onDismissRequest = {
                    showVerificationDialog = false
                    authViewModel.resetAuthState()
                    onRegisterSuccess()
                },
                title = { Text("Verification Email Sent", fontWeight = FontWeight.Bold) },
                text = { Text("We have sent a verification email to $email. Please verify your email before logging in.") },
                confirmButton = {
                    TextButton(onClick = {
                        showVerificationDialog = false
                        authViewModel.resetAuthState()
                        onRegisterSuccess()
                    }) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
