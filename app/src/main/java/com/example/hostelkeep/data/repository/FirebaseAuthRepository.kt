package com.example.hostelkeep.data.repository

import com.example.hostelkeep.data.MockData
import com.example.hostelkeep.model.User
import com.example.hostelkeep.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            fetchUserDataInternal(firebaseUser.uid)
        }
    }

    suspend fun login(email: String, role: UserRole): Result<User> {
        return try {
            try {
                val query = firestore.collection("users").whereEqualTo("email", email).get().await()
                if (!query.isEmpty) {
                    val doc = query.documents[0]
                    val user = doc.toObject(User::class.java)
                    val status = doc.getString("status") ?: user?.status ?: "ACTIVE"
                    if (status.equals("INACTIVE", true) || status.equals("SUSPENDED", true)) {
                        return Result.failure(Exception("Your account is currently inactive. Contact the administrator."))
                    }
                    if (user != null) {
                        val authRole = user.role
                        if (authRole != role) {
                            return Result.failure(Exception("This account is not registered for the selected role."))
                        }
                        _currentUser.value = user
                        return Result.success(user)
                    }
                }
            } catch (e: Exception) {
                // Offline fallback
            }

            val mockUser = MockData.initialUsers.find { it.email.equals(email, true) || it.role == role }
                ?: User("u_${System.currentTimeMillis()}", "User", email, role, status = "ACTIVE")
            
            if (mockUser.role != role) {
                return Result.failure(Exception("This account is not registered for the selected role."))
            }

            _currentUser.value = mockUser
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithEmailPassword(email: String, password: String, selectedRole: UserRole): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Authentication failed")
            val uid = firebaseUser.uid

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            val status = doc.getString("status") ?: user?.status ?: "ACTIVE"

            if (status.equals("INACTIVE", true) || status.equals("SUSPENDED", true)) {
                auth.signOut()
                return Result.failure(Exception("Your account is currently inactive. Contact the administrator."))
            }

            val authoritativeRole = user?.role ?: selectedRole
            if (authoritativeRole != selectedRole) {
                auth.signOut()
                return Result.failure(Exception("This account is not registered for the selected role."))
            }

            if (user != null) {
                val updatedUser = user.copy(status = status)
                _currentUser.value = updatedUser
                Result.success(updatedUser)
            } else {
                val defaultUser = User(id = uid, name = firebaseUser.email ?: email, email = email, role = selectedRole, status = "ACTIVE")
                _currentUser.value = defaultUser
                Result.success(defaultUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return loginWithEmailPassword(email, password, UserRole.STUDENT)
    }

    fun getCurrentUser(): FirebaseUser? {
        return try { auth.currentUser } catch (e: Exception) { null }
    }

    suspend fun registerStudent(name: String, email: String, rollNo: String, phone: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create user")
            val uid = firebaseUser.uid

            firebaseUser.sendEmailVerification().await()

            val userMap = hashMapOf<String, Any>(
                "id" to uid,
                "uid" to uid,
                "name" to name,
                "email" to email,
                "rollNo" to rollNo,
                "phone" to phone,
                "role" to UserRole.STUDENT.name,
                "status" to "ACTIVE",
                "createdAt" to FieldValue.serverTimestamp()
            )

            val studentMap = hashMapOf<String, Any>(
                "id" to uid,
                "uid" to uid,
                "name" to name,
                "email" to email,
                "rollNo" to rollNo,
                "phone" to phone,
                "parentName" to "",
                "parentPhone" to "",
                "bloodGroup" to "",
                "roomNo" to "",
                "bedNo" to "",
                "status" to "ACTIVE",
                "createdAt" to FieldValue.serverTimestamp()
            )

            firestore.collection("users").document(uid).set(userMap).await()
            firestore.collection("students").document(uid).set(studentMap).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String, role: UserRole, hostelName: String, roomNumber: String, phone: String): Result<User> {
        if (role == UserRole.ADMIN || role == UserRole.WARDEN || role == UserRole.SECURITY) {
            return Result.failure(Exception("Public registration is not allowed for Admin, Warden, or Security roles. Please contact an administrator."))
        }
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: "u_${System.currentTimeMillis()}"
            val newUser = User(id = uid, name = name, email = email, role = role, hostelName = hostelName, roomNumber = roomNumber, phone = phone)
            
            try {
                firestore.collection("users").document(uid).set(newUser).await()
            } catch (e: Exception) {
                // Ignore firestore write error if offline
            }
            
            _currentUser.value = newUser
            Result.success(newUser)
        } catch (e: Exception) {
            val newUser = User(id = "u_${System.currentTimeMillis()}", name = name, email = email, role = role, hostelName = hostelName, roomNumber = roomNumber, phone = phone)
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    fun switchRole(role: UserRole) {
        val found = MockData.initialUsers.find { it.role == role } ?: MockData.initialUsers.first()
        _currentUser.value = found
    }

    suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {}
        _currentUser.value = null
    }

    fun getFirebaseUser(): FirebaseUser? {
        return try { auth.currentUser } catch (e: Exception) { null }
    }

    suspend fun fetchUserData(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fetchUserDataInternal(uid: String) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                val user = doc.toObject(User::class.java)
                if (user != null) {
                    _currentUser.value = user
                }
            } catch (e: Exception) {}
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}
