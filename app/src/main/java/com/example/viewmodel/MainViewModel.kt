package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SurroundAudioEngine
import com.example.data.PresetRepository
import com.example.model.AudioChannel
import com.example.model.CinemaPreset
import com.example.model.DeviceRole
import com.example.model.SurroundDevice
import com.example.model.UserProfile
import com.example.network.DesktopBridgeServer
import com.example.network.WifiSurroundSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PresetRepository(application)
    val audioEngine = SurroundAudioEngine()
    val wifiSync = WifiSurroundSync(application)
    val desktopBridge = DesktopBridgeServer()

    // Navigation tab
    val currentTab = MutableStateFlow(0) // 0: Home / Cinema, 1: 3D Calibration, 2: Devices & Channels, 3: Desktop Bridge, 4: Profile

    // Current operating role of this device
    val selectedRole = MutableStateFlow(DeviceRole.CINEMA_HOST)

    // Current assigned channel if operating in satellite mode
    val mySatelliteChannel = MutableStateFlow(AudioChannel.FRONT_LEFT)

    val discoveredDevices: StateFlow<List<SurroundDevice>> = wifiSync.discoveredDevices
    val localIpAddress: StateFlow<String> = wifiSync.localIpAddress
    val isDiscovering: StateFlow<Boolean> = wifiSync.isDiscovering

    val isAudioPlaying: StateFlow<Boolean> = audioEngine.isPlaying
    val currentDemoTitle: StateFlow<String?> = audioEngine.currentDemoTitle
    val vuMeterLevels: StateFlow<Map<AudioChannel, Float>> = audioEngine.vuMeterLevels
    val activeTestingChannel: StateFlow<AudioChannel?> = audioEngine.activeTestingChannel

    val isDesktopServerRunning: StateFlow<Boolean> = desktopBridge.isRunning
    val presets: StateFlow<List<CinemaPreset>> = repository.presets
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    // Active preset
    val activePreset: StateFlow<CinemaPreset?> = combine(presets, userProfile) { presetList, profile ->
        presetList.find { it.id == profile.activePresetId } ?: presetList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        wifiSync.startDiscoveryAndSync()
        desktopBridge.startServer(8888)
    }

    fun setTab(index: Int) {
        currentTab.value = index
    }

    fun setDeviceRole(role: DeviceRole) {
        selectedRole.value = role
        wifiSync.currentRole = role
    }

    fun setSatelliteChannel(channel: AudioChannel) {
        mySatelliteChannel.value = channel
        wifiSync.currentChannel = channel
        val localDev = discoveredDevices.value.find { it.isLocalDevice }
        if (localDev != null) {
            wifiSync.updateDeviceChannel(localDev.id, channel)
        }
    }

    fun pingSpeaker(channel: AudioChannel, delayMs: Int = 0) {
        audioEngine.playChannelCalibrationPing(channel = channel, delayMs = delayMs)
    }

    fun testSpeakerDevice(device: SurroundDevice) {
        audioEngine.playChannelCalibrationPing(
            channel = device.assignedChannel,
            volume = device.volume,
            delayMs = device.totalDelayMs
        )
    }

    fun start360CalibrationSweep() {
        audioEngine.startSurroundCalibrationSweep()
    }

    fun startCinematicSpatialDemo() {
        if (audioEngine.isPlaying.value) {
            audioEngine.stopPlayback()
        } else {
            audioEngine.startSpatialCinematicDemo("4.1 / 5.1 Dolby Spatial Atmosphere")
        }
    }

    fun stopAudio() {
        audioEngine.stopPlayback()
    }

    fun updateDeviceChannel(deviceId: String, newChannel: AudioChannel) {
        wifiSync.updateDeviceChannel(deviceId, newChannel)
    }

    fun updateDeviceDistance(deviceId: String, distanceMeters: Float) {
        wifiSync.updateDeviceDistance(deviceId, distanceMeters)
    }

    fun updateDeviceDelayOffset(deviceId: String, offsetMs: Int) {
        wifiSync.updateDeviceDelayOffset(deviceId, offsetMs)
    }

    fun updateDeviceVolume(deviceId: String, volume: Float) {
        wifiSync.updateDeviceVolume(deviceId, volume)
    }

    fun addManualDevice(name: String, ip: String, channel: AudioChannel, platform: String) {
        wifiSync.addManualDevice(name, ip, channel, platform)
    }

    fun selectPreset(presetId: String) {
        repository.selectPreset(presetId)
    }

    fun toggleDesktopServer() {
        if (desktopBridge.isRunning.value) {
            desktopBridge.stopServer()
        } else {
            desktopBridge.startServer(8888)
        }
    }

    fun toggleCloudSync() {
        repository.toggleCloudSync()
    }

    fun setLanguage(lang: String) {
        repository.setLanguage(lang)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopPlayback()
        wifiSync.stopDiscovery()
        desktopBridge.stopServer()
    }
}
