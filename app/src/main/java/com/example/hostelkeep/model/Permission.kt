package com.example.hostelkeep.model

enum class Permission {
    VIEW_STUDENTS,
    MANAGE_STUDENTS,
    VIEW_ROOMS,
    MANAGE_ROOMS,
    ALLOCATE_ROOM,
    VIEW_ATTENDANCE,
    MANAGE_ATTENDANCE,
    CREATE_LEAVE,
    APPROVE_LEAVE,
    CREATE_OUTPASS,
    APPROVE_OUTPASS,
    MANAGE_FEES,
    CREATE_COMPLAINT,
    MANAGE_COMPLAINTS,
    MANAGE_VISITORS,
    VIEW_EMERGENCY,
    MANAGE_EMERGENCY,
    VIEW_REPORTS,
    MANAGE_USERS
}

object RolePermissions {
    private val adminPermissions = Permission.entries.toSet()

    private val wardenPermissions = setOf(
        Permission.VIEW_STUDENTS,
        Permission.VIEW_ROOMS,
        Permission.ALLOCATE_ROOM,
        Permission.VIEW_ATTENDANCE,
        Permission.MANAGE_ATTENDANCE,
        Permission.APPROVE_LEAVE,
        Permission.APPROVE_OUTPASS,
        Permission.MANAGE_COMPLAINTS,
        Permission.MANAGE_VISITORS,
        Permission.VIEW_EMERGENCY,
        Permission.MANAGE_EMERGENCY,
        Permission.VIEW_REPORTS
    )

    private val studentPermissions = setOf(
        Permission.VIEW_ROOMS,
        Permission.VIEW_ATTENDANCE,
        Permission.CREATE_LEAVE,
        Permission.CREATE_OUTPASS,
        Permission.CREATE_COMPLAINT,
        Permission.VIEW_EMERGENCY
    )

    private val parentPermissions = setOf(
        Permission.VIEW_ATTENDANCE,
        Permission.CREATE_LEAVE,
        Permission.VIEW_EMERGENCY
    )

    private val securityPermissions = setOf(
        Permission.APPROVE_OUTPASS,
        Permission.MANAGE_VISITORS,
        Permission.VIEW_EMERGENCY
    )

    fun getPermissionsForRole(role: UserRole): Set<Permission> {
        return when (role) {
            UserRole.ADMIN -> adminPermissions
            UserRole.WARDEN -> wardenPermissions
            UserRole.STUDENT -> studentPermissions
            UserRole.PARENT -> parentPermissions
            UserRole.SECURITY -> securityPermissions
        }
    }
}

fun hasPermission(role: UserRole, permission: Permission): Boolean {
    return RolePermissions.getPermissionsForRole(role).contains(permission)
}
