package com.smartsite.app.data.repository

import com.smartsite.app.data.model.DroneSession
import kotlinx.coroutines.flow.StateFlow

interface DroneRepository {
    val session: StateFlow<DroneSession?>
    fun createSession(operatorEmail: String): DroneSession
    fun update(transform: (DroneSession) -> DroneSession)
}
