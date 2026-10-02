package com.smartsite.app.data.mock

import com.smartsite.app.data.model.DroneSession
import com.smartsite.app.data.model.DroneStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory replacement for the Base44 SDK `DroneSession` entity.
 * Holds at most one session, mirroring the mockup's single live feed.
 */
object DroneRepository {

    private val _session = MutableStateFlow<DroneSession?>(null)
    val session: StateFlow<DroneSession?> = _session.asStateFlow()

    fun createSession(operatorEmail: String): DroneSession {
        val session = DroneSession(
            status = DroneStatus.IDLE,
            batteryLevel = 100,
            altitude = 0f,
            gpsLat = 48.8566,
            gpsLng = 2.3522,
            missionName = "Nouvelle mission",
            streamShared = false,
            operatorEmail = operatorEmail
        )
        _session.value = session
        return session
    }

    fun update(transform: (DroneSession) -> DroneSession) {
        _session.value = _session.value?.let(transform)
    }
}
