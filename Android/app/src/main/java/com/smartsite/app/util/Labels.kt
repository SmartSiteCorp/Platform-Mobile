package com.smartsite.app.util

import com.smartsite.app.data.model.UserRole

/** French display label for a user role. */
fun UserRole.frenchLabel(): String = when (this) {
    UserRole.ADMIN -> "Administrateur"
    UserRole.ARCHITECT -> "Architecte"
    UserRole.WORKER -> "Ouvrier"
    UserRole.PROJECT_MANAGER -> "Chef de projet"
    UserRole.DRONE_OPERATOR -> "Droniste"
}
