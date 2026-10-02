package com.smartsite.app.data.model

enum class UserRole { ADMIN, ARCHITECT, WORKER, PROJECT_MANAGER, DRONE_OPERATOR }

data class User(
    val fullName: String,
    val email: String,
    val role: UserRole,
    val phone: String,
    val specialty: String
)
