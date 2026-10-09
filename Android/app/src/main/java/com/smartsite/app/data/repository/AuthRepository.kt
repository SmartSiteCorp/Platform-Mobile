package com.smartsite.app.data.repository

import com.smartsite.app.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User>
    fun update(user: User)
}
