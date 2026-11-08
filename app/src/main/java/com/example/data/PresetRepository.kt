package com.example.data

import android.content.Context
import com.example.model.CinemaPreset
import com.example.model.SystemPresetType
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PresetRepository(context: Context) {

    private val prefs = context.getSharedPreferences("dolby_shiblu_prefs", Context.MODE_PRIVATE)

    private val defaultPresets = listOf(
        CinemaPreset(
            id = "preset_shiblu_cinema",
            name = "Shiblu's 4-Phone Cinema",
            description = "Optimized for 4 phones around sofa + Laptop in center for dialogue and movie screen.",
            type = SystemPresetType.FOUR_PHONE_ONE_LAPTOP,
            frontLeftDistance = 1.7f,
            frontRightDistance = 1.7f,
            surroundLeftDistance = 1.4f,
            surroundRightDistance = 1.4f,
            bassBoostDb = 5.0f,
            dialogueClarity = true,
            isDefault = true
        ),
        CinemaPreset(
            id = "preset_quad_spatial",
            name = "360° Quad Spatial Studio",
            description = "Pure quadraphonic acoustic balance for 4 corner phone speakers.",
            type = SystemPresetType.QUADRAPHONIC_SURROUND,
            frontLeftDistance = 2.0f,
            frontRightDistance = 2.0f,
            surroundLeftDistance = 2.0f,
            surroundRightDistance = 2.0f,
            bassBoostDb = 3.0f,
            dialogueClarity = false,
            isDefault = false
        ),
        CinemaPreset(
            id = "preset_virtual_51",
            name = "Dolby 5.1 Virtual Home Theater",
            description = "Virtual Center & Subwoofer acoustic routing combined with 4 physical phone satellites.",
            type = SystemPresetType.FULL_FIVE_ONE,
            frontLeftDistance = 1.8f,
            frontRightDistance = 1.8f,
            surroundLeftDistance = 1.6f,
            surroundRightDistance = 1.6f,
            bassBoostDb = 6.5f,
            dialogueClarity = true,
            isDefault = false
        )
    )

    private val _presets = MutableStateFlow<List<CinemaPreset>>(defaultPresets)
    val presets: StateFlow<List<CinemaPreset>> = _presets.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Shiblu Hasan",
            email = "shibluhasan323@gmail.com",
            activePresetId = "preset_shiblu_cinema",
            isCloudSyncEnabled = true,
            isOfflineModeActive = false,
            lastSyncTime = "Synced 2m ago",
            totalSyncedSessions = 18,
            language = "English"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun selectPreset(presetId: String) {
        val current = _presets.value
        val updated = current.map { it.copy(isDefault = it.id == presetId) }
        _presets.value = updated
        _userProfile.value = _userProfile.value.copy(activePresetId = presetId)
    }

    fun updateActivePresetDistances(fl: Float, fr: Float, sl: Float, sr: Float) {
        val activeId = _userProfile.value.activePresetId
        val current = _presets.value
        _presets.value = current.map { preset ->
            if (preset.id == activeId) {
                preset.copy(
                    frontLeftDistance = fl,
                    frontRightDistance = fr,
                    surroundLeftDistance = sl,
                    surroundRightDistance = sr
                )
            } else preset
        }
    }

    fun updateBassAndDialogue(bassBoostDb: Float, dialogueClarity: Boolean) {
        val activeId = _userProfile.value.activePresetId
        val current = _presets.value
        _presets.value = current.map { preset ->
            if (preset.id == activeId) {
                preset.copy(
                    bassBoostDb = bassBoostDb,
                    dialogueClarity = dialogueClarity
                )
            } else preset
        }
    }

    fun toggleCloudSync() {
        val current = _userProfile.value
        val newSync = !current.isCloudSyncEnabled
        _userProfile.value = current.copy(
            isCloudSyncEnabled = newSync,
            lastSyncTime = if (newSync) "Just synced with Cloud" else "Offline Local Cache",
            isOfflineModeActive = !newSync
        )
    }

    fun setLanguage(language: String) {
        _userProfile.value = _userProfile.value.copy(language = language)
    }
}
