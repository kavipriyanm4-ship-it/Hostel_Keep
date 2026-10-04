package com.example.hostelkeep.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT, // ADMIN, WARDEN, STUDENT, PARENT, SECURITY
    val hostelName: String = "Block A - Kaveri Hostel",
    val roomNumber: String = "101",
    val phone: String = "+91 9876543210",
    val avatarUrl: String = "",
    val status: String = "ACTIVE"
)

enum class UserRole {
    ADMIN, WARDEN, STUDENT, PARENT, SECURITY
}

data class Student(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val rollNo: String = "",
    val admissionNumber: String = "",
    val phone: String = "",
    val parentName: String = "",
    val parentPhone: String = "",
    val bloodGroup: String = "",
    val roomNo: String = "",
    val bedNo: String = "",
    val hostelId: String = "",
    val roomId: String = "",
    val bedId: String = "",
    val department: String = "",
    val course: String = "",
    val year: Int = 1,
    val profileImageUrl: String = "",
    val address: String = "",
    val status: String = "ACTIVE",
    val createdAt: Any? = null,
    val updatedAt: Any? = null
)

data class Hostel(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val description: String = "",
    val wardenId: String = ""
)

data class Building(
    val id: String = "",
    val hostelId: String = "",
    val name: String = "",
    val code: String = ""
)

data class Floor(
    val id: String = "",
    val buildingId: String = "",
    val hostelId: String = "",
    val floorNumber: Int = 0,
    val name: String = ""
)

data class Room(
    val id: String = "",
    val floorId: String = "",
    val buildingId: String = "",
    val hostelId: String = "",
    val roomNo: String = "",
    val roomNumber: String = "",
    val block: String = "",
    val capacity: Int = 0,
    val occupied: Int = 0,
    val occupiedBeds: Int = 0,
    val type: String = "Non-AC", // AC / Non-AC
    val occupants: List<String> = emptyList(),
    val status: String = "AVAILABLE"
)

data class Bed(
    val id: String = "",
    val roomId: String = "",
    val bedNo: String = "",
    val bedNumber: String = "",
    val status: String = "AVAILABLE", // AVAILABLE, OCCUPIED, MAINTENANCE
    val studentId: String? = null
)

data class RoomAllocation(
    val id: String = "",
    val studentId: String = "",
    val admissionNumber: String = "",
    val hostelId: String = "",
    val roomId: String = "",
    val bedId: String = "",
    val allocatedBy: String = "",
    val allocatedAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE" // ACTIVE, VACATED
)

data class AuditLog(
    val id: String = "",
    val action: String = "", // BED_ALLOCATED, BED_VACATED, etc.
    val performedBy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val details: Map<String, String> = emptyMap()
)

data class Attendance(
    val id: String = "",
    val attendanceId: String = "",
    val studentId: String = "",
    val hostelId: String = "",
    val roomId: String = "",
    val bedId: String = "",
    val studentName: String = "",
    val rollNo: String = "",
    val date: String = "",
    val status: AttendanceStatus = AttendanceStatus.PRESENT, // PRESENT, ABSENT, LATE, EXCUSED
    val markedBy: String = "",
    val markedByRole: String = "",
    val markedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val updatedBy: String = ""
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXCUSED
}

data class Leave(
    val id: String = "",
    val requestId: String = "",
    val studentId: String = "",
    val hostelId: String = "",
    val roomId: String = "",
    val studentName: String = "",
    val rollNo: String = "",
    val leaveType: LeaveType = LeaveType.HOME,
    val fromDate: String = "",
    val toDate: String = "",
    val reason: String = "",
    val destination: String = "",
    val contactNumber: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val reviewedBy: String = "",
    val reviewedAt: Long? = null,
    val reviewComment: String = ""
)

enum class LeaveType {
    HOME, MEDICAL, PERSONAL, EMERGENCY, OTHER
}

enum class RequestStatus {
    PENDING, APPROVED, REJECTED, CANCELLED, EXPIRED, CHECKED_OUT, RETURNED
}

data class Outpass(
    val id: String = "",
    val hostelId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNo: String = "",
    val destination: String = "",
    val outTime: String = "",
    val inTime: String = "",
    val reason: String = "",
    val emergencyContact: String = "",
    val qrCodeData: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val exitTime: Long? = null,
    val returnTime: Long? = null,
    val appliedAt: Long = System.currentTimeMillis(),
    val isCheckedOut: Boolean = false,
    val isCheckedIn: Boolean = false
)

data class Fee(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNo: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val dueDate: String = "",
    val status: FeeStatus = FeeStatus.PENDING, // PAID, PENDING, OVERDUE
    val paidDate: String? = null,
    val receiptNo: String? = null
)

enum class FeeStatus {
    PAID, PENDING, OVERDUE
}

data class Complaint(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val roomNo: String = "",
    val category: String = "", // Electrical, Plumbing, Internet, Furniture, Cleanliness
    val description: String = "",
    val status: ComplaintStatus = ComplaintStatus.OPEN, // OPEN, IN_PROGRESS, RESOLVED
    val date: String = "",
    val priority: String = ""
)

enum class ComplaintStatus {
    OPEN, IN_PROGRESS, RESOLVED
}

data class Maintenance(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val status: ComplaintStatus = ComplaintStatus.OPEN,
    val reportedDate: String = "",
    val assignedTo: String = ""
)

data class MessMenu(
    val id: String = "",
    val dayOfWeek: String = "",
    val breakfast: String = "",
    val lunch: String = "",
    val snacks: String = "",
    val dinner: String = "",
    val rating: Float = 4.2f,
    val totalReviews: Int = 120
)

data class Visitor(
    val id: String = "",
    val visitorName: String = "",
    val phone: String = "",
    val studentNameToVisit: String = "",
    val relation: String = "",
    val entryTime: String = "",
    val exitTime: String? = null,
    val purpose: String = "",
    val passNo: String = ""
)

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val date: String = "",
    val category: String = "",
    val isRead: Boolean = false
)

data class Emergency(
    val id: String = "",
    val studentName: String = "",
    val rollNo: String = "",
    val roomNo: String = "",
    val emergencyType: String = "",
    val time: String = "",
    val status: String = ""
)
