package com.smartsite.app.data.mock

import com.smartsite.app.data.model.User
import com.smartsite.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory replacement for the Base44 SDK `auth.me()` call.
 * The alpha uses a single fixed user (admin role).
 */
object MockAuthRepository {

    private val _currentUser = MutableStateFlow(
        User(
            fullName = "Marie Dupont",
            email = "marie.dupont@smartsite.fr",
            role = UserRole.ADMIN,
            phone = "06 12 34 56 78",
            specialty = "Conductrice de travaux"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun update(user: User) {
        _currentUser.value = user
    }
}
