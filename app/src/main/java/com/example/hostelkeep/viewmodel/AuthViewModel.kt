package com.example.hostelkeep.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hostelkeep.data.repository.FirebaseAuthRepository
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: FirebaseAuthRepository = FirebaseAuthRepository()
) : ViewModel() {
    val currentUser: StateFlow<User?> = authRepository.currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, role)
            result.onSuccess {
                _authState.value = AuthState.Success("Login successful")
            }.onFailure { e ->
                _authState.value = AuthState.Error(getFriendlyErrorMessage(e))
            }
        }
    }

    fun loginWithEmailPassword(email: String, password: String, selectedRole: UserRole) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithEmailPassword(email, password, selectedRole)
            result.onSuccess {
                _authState.value = AuthState.Success("Login successful")
            }.onFailure { e ->
                _authState.value = AuthState.Error(getFriendlyErrorMessage(e))
            }
        }
    }

    fun registerStudent(name: String, email: String, rollNo: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.registerStudent(name, email, rollNo, phone, password)
            result.onSuccess {
                _authState.value = AuthState.Success("Registration successful! Verification email sent.")
            }.onFailure { e ->
                _authState.value = AuthState.Error(getFriendlyErrorMessage(e))
            }
        }
    }

    fun register(email: String, password: String, name: String, role: UserRole, hostelName: String, roomNumber: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password, name, role, hostelName, roomNumber, phone)
            result.onSuccess {
                _authState.value = AuthState.Success("Registration successful")
            }.onFailure { e ->
                _authState.value = AuthState.Error(getFriendlyErrorMessage(e))
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    private fun getFriendlyErrorMessage(e: Throwable): String {
        val errorCode = (e as? FirebaseAuthException)?.errorCode ?: ""
        val msg = e.message ?: ""
        return when {
            msg.contains("This account is not registered for the selected role.", true) -> "This account is not registered for the selected role."
            errorCode.contains("EMAIL_ALREADY_IN_USE", true) || msg.contains("email-already-in-use", true) -> "This email is already registered. Please log in."
            errorCode.contains("INVALID_EMAIL", true) || msg.contains("invalid-email", true) -> "The email address is invalid."
            errorCode.contains("WRONG_PASSWORD", true) || errorCode.contains("INVALID_CREDENTIAL", true) || msg.contains("wrong-password", true) || msg.contains("invalid-credential", true) -> "Incorrect email or password."
            errorCode.contains("USER_NOT_FOUND", true) || msg.contains("user-not-found", true) -> "No account found with this email."
            errorCode.contains("WEAK_PASSWORD", true) || msg.contains("weak-password", true) -> "The password is too weak (minimum 6 characters)."
            errorCode.contains("NETWORK_REQUEST_FAILED", true) || msg.contains("network-request-failed", true) -> "Network error. Please check your internet connection."
            else -> e.localizedMessage ?: "Authentication failed"
        }
    }

    fun switchRole(role: UserRole) {
        authRepository.switchRole(role)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
        }
    }
}
