package com.example.model

data class SurroundDevice(
    val id: String,
    val name: String,
    val ip: String,
    val port: Int = 9872,
    val assignedChannel: AudioChannel,
    val isLocalDevice: Boolean = false,
    val isOnline: Boolean = true,
    val latencyMs: Int = 12,
    val delayOffsetMs: Int = 0,
    val distanceMeters: Float = 1.6f,
    val volume: Float = 1.0f,
    val battery: Int = 85,
    val platform: String = "Android",
    val signalDbm: Int = -48,
    val role: DeviceRole = DeviceRole.SATELLITE_RECEIVER
) {
    /**
     * Automatic speed-of-sound delay compensation in milliseconds.
     * Speed of sound in air ≈ 343 m/s = 0.343 m/ms.
     * Delay (ms) = distance (m) / 0.343
     */
    val acousticDelayMs: Int
        get() = (distanceMeters / 0.343f).toInt()

    val totalDelayMs: Int
        get() = acousticDelayMs + delayOffsetMs
}

data class CinemaPreset(
    val id: String,
    val name: String,
    val description: String,
    val type: SystemPresetType,
    val frontLeftDistance: Float = 1.8f,
    val frontRightDistance: Float = 1.8f,
    val surroundLeftDistance: Float = 1.5f,
    val surroundRightDistance: Float = 1.5f,
    val bassBoostDb: Float = 4.0f,
    val dialogueClarity: Boolean = true,
    val isDefault: Boolean = false
)

data class UserProfile(
    val name: String = "Shiblu Hasan",
    val email: String = "shibluhasan323@gmail.com",
    val activePresetId: String = "preset_shiblu_cinema",
    val isCloudSyncEnabled: Boolean = true,
    val isOfflineModeActive: Boolean = false,
    val lastSyncTime: String = "Just now",
    val totalSyncedSessions: Int = 42,
    val language: String = "English" // "English" or "বাংলা"
)
