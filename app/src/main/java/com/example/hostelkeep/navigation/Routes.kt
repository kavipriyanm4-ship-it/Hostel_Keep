package com.example.hostelkeep.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ROLE_SELECTION = "role_selection"

    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val WARDEN_DASHBOARD = "warden_dashboard"
    const val STUDENT_DASHBOARD = "student_dashboard"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val SECURITY_DASHBOARD = "security_dashboard"

    const val ADMIN_REPORTS = "admin_reports"
    const val ADMIN_SETTINGS = "admin_settings"
    const val WARDEN_ANNOUNCEMENTS = "warden_announcements"
    const val STUDENT_PROFILE = "student_profile"
    const val HOSTEL_RULES = "hostel_rules"

    const val STUDENTS_LIST = "students_list"
    const val ADD_STUDENT = "add_student"
    const val STUDENT_DETAILS = "student_details/{studentId}"
    fun studentDetails(id: String) = "student_details/$id"

    const val ROOMS_LIST = "rooms_list"
    const val ADD_ROOM = "add_room"
    const val ROOM_DETAILS = "room_details/{roomId}"
    fun roomDetails(id: String) = "room_details/$id"
    const val ROOM_ALLOCATION = "room_allocation"

    const val ATTENDANCE_DASHBOARD = "attendance_dashboard"
    const val STUDENT_ATTENDANCE = "student_attendance"

    const val LEAVE_DASHBOARD = "leave_dashboard"
    const val APPLY_LEAVE = "apply_leave"

    const val OUTPASS_DASHBOARD = "outpass_dashboard"
    const val APPLY_OUTPASS = "apply_outpass"
    const val QR_OUTPASS = "qr_outpass/{outpassId}"
    fun qrOutpass(id: String) = "qr_outpass/$id"

    const val FEE_DASHBOARD = "fee_dashboard"
    const val STUDENT_FEE = "student_fee"

    const val COMPLAINT_DASHBOARD = "complaint_dashboard"
    const val CREATE_COMPLAINT = "create_complaint"
    const val COMPLAINT_DETAILS = "complaint_details/{complaintId}"
    fun complaintDetails(id: String) = "complaint_details/$id"

    const val MAINTENANCE_DASHBOARD = "maintenance_dashboard"
    const val CREATE_MAINTENANCE = "create_maintenance"

    const val MESS_DASHBOARD = "mess_dashboard"
    const val MESS_FEEDBACK = "mess_feedback"

    const val VISITOR_DASHBOARD = "visitor_dashboard"
    const val REGISTER_VISITOR = "register_visitor"
    const val QR_SCANNER = "qr_scanner"
    const val VISITOR_LOG = "visitor_log"

    const val NOTIFICATION_SCREEN = "notification_screen"

    const val EMERGENCY_SCREEN = "emergency_screen"
    const val EMERGENCY_DASHBOARD = "emergency_dashboard"

    const val REPORTS_SCREEN = "reports_screen"
}
