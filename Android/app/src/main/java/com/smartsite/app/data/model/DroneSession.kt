package com.smartsite.app.data.model

enum class DroneStatus { IDLE, FLYING, PAUSED, RETURNING, LANDED }

data class DroneSession(
    val status: DroneStatus,
    val batteryLevel: Int, // 0..100
    val altitude: Float,   // meters
    val gpsLat: Double,
    val gpsLng: Double,
    val missionName: String,
    val streamShared: Boolean,
    val operatorEmail: String
)
