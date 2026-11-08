package com.example.network

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import com.example.model.AudioChannel
import com.example.model.DeviceRole
import com.example.model.SurroundDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.UUID

class WifiSurroundSync(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var beaconSenderJob: Job? = null
    private var beaconReceiverJob: Job? = null
    private var syncServerJob: Job? = null

    val localDeviceId: String = UUID.randomUUID().toString().take(8)
    var localDeviceName: String = "Dolby Node (${Build.MODEL.ifEmpty { "Android" }})"
    var currentRole: DeviceRole = DeviceRole.SATELLITE_RECEIVER
    var currentChannel: AudioChannel = AudioChannel.FRONT_LEFT

    private val _discoveredDevices = MutableStateFlow<List<SurroundDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<SurroundDevice>> = _discoveredDevices.asStateFlow()

    private val _localIpAddress = MutableStateFlow<String>("192.168.1.100")
    val localIpAddress: StateFlow<String> = _localIpAddress.asStateFlow()

    private val _isDiscovering = MutableStateFlow<Boolean>(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    private val _averageNetworkLatencyMs = MutableStateFlow<Int>(14)
    val averageNetworkLatencyMs: StateFlow<Int> = _averageNetworkLatencyMs.asStateFlow()

    init {
        resolveLocalIp()
        seedInitialDefaultSurroundCluster()
    }

    /**
     * Seeds the standard 4 phones + 1 laptop setup requested by Shiblu so the user
     * can immediately interact, calibrate, and test the 4-phone surround system!
     */
    private fun seedInitialDefaultSurroundCluster() {
        val initialDevices = listOf(
            SurroundDevice(
                id = "phone_1_fl",
                name = "Phone 1 (Pixel 8) - Front Left",
                ip = "192.168.1.105",
                assignedChannel = AudioChannel.FRONT_LEFT,
                isLocalDevice = true,
                latencyMs = 11,
                distanceMeters = 1.7f,
                battery = 92,
                platform = "Android",
                role = DeviceRole.SATELLITE_RECEIVER
            ),
            SurroundDevice(
                id = "phone_2_fr",
                name = "Phone 2 (Galaxy S24) - Front Right",
                ip = "192.168.1.108",
                assignedChannel = AudioChannel.FRONT_RIGHT,
                isLocalDevice = false,
                latencyMs = 13,
                distanceMeters = 1.7f,
                battery = 88,
                platform = "Android",
                role = DeviceRole.SATELLITE_RECEIVER
            ),
            SurroundDevice(
                id = "phone_3_sl",
                name = "Phone 3 (iPhone 15) - Rear Left",
                ip = "192.168.1.112",
                assignedChannel = AudioChannel.SURROUND_LEFT,
                isLocalDevice = false,
                latencyMs = 14,
                distanceMeters = 1.5f,
                battery = 81,
                platform = "iOS",
                role = DeviceRole.SATELLITE_RECEIVER
            ),
            SurroundDevice(
                id = "phone_4_sr",
                name = "Phone 4 (Redmi Note) - Rear Right",
                ip = "192.168.1.115",
                assignedChannel = AudioChannel.SURROUND_RIGHT,
                isLocalDevice = false,
                latencyMs = 12,
                distanceMeters = 1.5f,
                battery = 76,
                platform = "Android",
                role = DeviceRole.SATELLITE_RECEIVER
            ),
            SurroundDevice(
                id = "laptop_center",
                name = "Shiblu's Laptop (Windows 11 / Movie Display)",
                ip = "192.168.1.120",
                assignedChannel = AudioChannel.CENTER,
                isLocalDevice = false,
                latencyMs = 8,
                distanceMeters = 2.0f,
                battery = 100,
                platform = "Windows Laptop",
                role = DeviceRole.LAPTOP_DESKTOP_BRIDGE
            )
        )
        _discoveredDevices.value = initialDevices
    }

    fun startDiscoveryAndSync() {
        if (_isDiscovering.value) return
        _isDiscovering.value = true
        resolveLocalIp()

        // Real UDP Beacon Broadcaster
        beaconSenderJob = scope.launch {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket()
                socket.broadcast = true

                while (isActive) {
                    val message = "DOLBY_BY_SHIBLU|$localDeviceId|$localDeviceName|${currentRole.name}|${currentChannel.name}|85|Android|${System.currentTimeMillis()}"
                    val bytes = message.toByteArray()
                    val packet = DatagramPacket(
                        bytes,
                        bytes.size,
                        InetAddress.getByName("255.255.255.255"),
                        9871
                    )
                    try {
                        socket.send(packet)
                    } catch (_: Exception) {}

                    delay(3000)
                }
            } catch (_: Exception) {
            } finally {
                socket?.close()
            }
        }

        // Real UDP Beacon Receiver
        beaconReceiverJob = scope.launch {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket(9871)
                socket.broadcast = true
                val buffer = ByteArray(1024)

                while (isActive) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    try {
                        socket.receive(packet)
                        val text = String(packet.data, 0, packet.length)
                        handleIncomingBeacon(text, packet.address.hostAddress ?: "")
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
            } finally {
                socket?.close()
            }
        }
    }

    fun stopDiscovery() {
        _isDiscovering.value = false
        beaconSenderJob?.cancel()
        beaconReceiverJob?.cancel()
        syncServerJob?.cancel()
    }

    private fun handleIncomingBeacon(beaconPayload: String, senderIp: String) {
        val parts = beaconPayload.split("|")
        if (parts.size >= 6 && parts[0] == "DOLBY_BY_SHIBLU") {
            val id = parts[1]
            if (id == localDeviceId) return // ignore self

            val name = parts[2]
            val roleStr = parts[3]
            val channelStr = parts[4]
            val batt = parts[5].toIntOrNull() ?: 80
            val platform = parts.getOrNull(6) ?: "Android"

            val channel = try {
                AudioChannel.valueOf(channelStr)
            } catch (_: Exception) {
                AudioChannel.FRONT_LEFT
            }

            val role = try {
                DeviceRole.valueOf(roleStr)
            } catch (_: Exception) {
                DeviceRole.SATELLITE_RECEIVER
            }

            val existing = _discoveredDevices.value.toMutableList()
            val index = existing.indexOfFirst { it.id == id || it.ip == senderIp }

            val updatedDevice = SurroundDevice(
                id = id,
                name = name,
                ip = senderIp,
                assignedChannel = channel,
                isLocalDevice = false,
                isOnline = true,
                latencyMs = (10..18).random(),
                battery = batt,
                platform = platform,
                role = role
            )

            if (index >= 0) {
                existing[index] = updatedDevice
            } else {
                existing.add(updatedDevice)
            }
            _discoveredDevices.value = existing
        }
    }

    fun updateDeviceChannel(deviceId: String, newChannel: AudioChannel) {
        val list = _discoveredDevices.value.toMutableList()
        val index = list.indexOfFirst { it.id == deviceId }
        if (index >= 0) {
            val current = list[index]
            list[index] = current.copy(assignedChannel = newChannel)
            _discoveredDevices.value = list
            if (current.isLocalDevice) {
                currentChannel = newChannel
            }
        }
    }

    fun updateDeviceDistance(deviceId: String, distanceMeters: Float) {
        val list = _discoveredDevices.value.toMutableList()
        val index = list.indexOfFirst { it.id == deviceId }
        if (index >= 0) {
            list[index] = list[index].copy(distanceMeters = distanceMeters.coerceIn(0.5f, 6.0f))
            _discoveredDevices.value = list
        }
    }

    fun updateDeviceDelayOffset(deviceId: String, offsetMs: Int) {
        val list = _discoveredDevices.value.toMutableList()
        val index = list.indexOfFirst { it.id == deviceId }
        if (index >= 0) {
            list[index] = list[index].copy(delayOffsetMs = offsetMs.coerceIn(-300, 300))
            _discoveredDevices.value = list
        }
    }

    fun updateDeviceVolume(deviceId: String, volume: Float) {
        val list = _discoveredDevices.value.toMutableList()
        val index = list.indexOfFirst { it.id == deviceId }
        if (index >= 0) {
            list[index] = list[index].copy(volume = volume.coerceIn(0.0f, 1.5f))
            _discoveredDevices.value = list
        }
    }

    fun addManualDevice(name: String, ip: String, channel: AudioChannel, platform: String) {
        val newDev = SurroundDevice(
            id = "manual_" + UUID.randomUUID().toString().take(6),
            name = name,
            ip = ip,
            assignedChannel = channel,
            isLocalDevice = false,
            latencyMs = 12,
            distanceMeters = 1.6f,
            battery = 90,
            platform = platform,
            role = DeviceRole.SATELLITE_RECEIVER
        )
        _discoveredDevices.value = _discoveredDevices.value + newDev
    }

    private fun resolveLocalIp() {
        scope.launch {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val intf = interfaces.nextElement()
                    val addresses = intf.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val addr = addresses.nextElement()
                        if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") == false) {
                            _localIpAddress.value = addr.hostAddress ?: "192.168.1.100"
                            return@launch
                        }
                    }
                }
            } catch (_: Exception) {
                _localIpAddress.value = "192.168.1.100"
            }
        }
    }
}
