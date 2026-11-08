package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioChannel
import com.example.model.SurroundDevice
import com.example.ui.components.ChannelBadge
import com.example.ui.theme.DolbyBorder
import com.example.ui.theme.DolbyCardDark
import com.example.ui.theme.DolbyCardElevated
import com.example.ui.theme.DolbyGold
import com.example.ui.theme.SpatialCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@Composable
fun DevicesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.discoveredDevices.collectAsState()
    val isDiscovering by viewModel.isDiscovering.collectAsState()
    val localIp by viewModel.localIpAddress.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isBangla = userProfile.language == "বাংলা"

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "ডিভাইস ও চ্যানেল অ্যাসাইনমেন্ট" else "Devices & Channel Matrix",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBangla) "লোকাল আইপি: $localIp (ওয়াই-ফাই ৫জি)" else "Local Wi-Fi IP: $localIp (Port: 9871/9872)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SpatialCyan
                )
            }

            Row {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DolbyGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_manual_device_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBangla) "যুক্ত করুন" else "Add",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Channel Matrix Swapper
        Card(
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = DolbyGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "দ্রুত চ্যানেল অদলবদল (Swap L/R)" else "Quick Channel Inversion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        // Swap Front Left and Front Right
                        val fl = devices.find { it.assignedChannel == AudioChannel.FRONT_LEFT }
                        val fr = devices.find { it.assignedChannel == AudioChannel.FRONT_RIGHT }
                        if (fl != null && fr != null) {
                            viewModel.updateDeviceChannel(fl.id, AudioChannel.FRONT_RIGHT)
                            viewModel.updateDeviceChannel(fr.id, AudioChannel.FRONT_LEFT)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DolbyGold),
                    modifier = Modifier.testTag("swap_front_channels_btn")
                ) {
                    Text(
                        text = if (isBangla) "FL ⇄ FR" else "Swap FL/FR",
                        color = DolbyGold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(devices, key = { it.id }) { dev ->
                DeviceRowItem(
                    device = dev,
                    isBangla = isBangla,
                    onChannelChange = { newCh ->
                        viewModel.updateDeviceChannel(dev.id, newCh)
                    },
                    onPing = {
                        viewModel.testSpeakerDevice(dev)
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddDeviceDialog(
            isBangla = isBangla,
            onDismiss = { showAddDialog = false },
            onAdd = { name, ip, ch, os ->
                viewModel.addManualDevice(name, ip, ch, os)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DeviceRowItem(
    device: SurroundDevice,
    isBangla: Boolean,
    onChannelChange: (AudioChannel) -> Unit,
    onPing: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("device_row_${device.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable { menuExpanded = true }) {
                        ChannelBadge(
                            channel = device.assignedChannel,
                            isHighlighted = true
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(DolbyCardElevated)
                        ) {
                            AudioChannel.entries.forEach { ch ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${ch.code} - ${if (isBangla) ch.titleBn else ch.title}",
                                            color = ch.getColor(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {
                                        onChannelChange(ch)
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (device.isLocalDevice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DolbyGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "নিজ ফোন" else "Host",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DolbyGold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${device.platform} • ${device.ip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Battery indicator
                    Icon(
                        imageVector = Icons.Default.BatteryStd,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${device.battery}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Ping button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(device.assignedChannel.getColor())
                            .clickable { onPing() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("ping_btn_${device.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ping",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-metrics row (Latency, Distance, Speed-of-sound Delay)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DolbyCardElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wi-Fi: ${device.latencyMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusGreen
                )
                Text(
                    text = "Dist: ${device.distanceMeters}m",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpatialCyan
                )
                Text(
                    text = "Acoustic Delay: ${device.totalDelayMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = DolbyGold
                )
                Text(
                    text = "Vol: ${(device.volume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun AddDeviceDialog(
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onAdd: (name: String, ip: String, channel: AudioChannel, platform: String) -> Unit
) {
    var name by remember { mutableStateOf("Redmi Note 13") }
    var ip by remember { mutableStateOf("192.168.1.125") }
    var selectedChannel by remember { mutableStateOf(AudioChannel.FRONT_LEFT) }
    var platform by remember { mutableStateOf("Android") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DolbyCardDark,
        title = {
            Text(
                text = if (isBangla) "নতুন ফোন / স্পিকার যুক্ত করুন" else "Connect Phone / Laptop",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DolbyGold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Device Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DolbyGold,
                        unfocusedBorderColor = DolbyBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Address") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SpatialCyan,
                        unfocusedBorderColor = DolbyBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (isBangla) "চ্যানেল সিলেক্ট করুন:" else "Assign Channel:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        AudioChannel.FRONT_LEFT,
                        AudioChannel.FRONT_RIGHT,
                        AudioChannel.SURROUND_LEFT,
                        AudioChannel.SURROUND_RIGHT
                    ).forEach { ch ->
                        val isSel = selectedChannel == ch
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ch.getColor().copy(alpha = 0.3f) else DolbyCardElevated)
                                .border(1.dp, if (isSel) ch.getColor() else DolbyBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedChannel = ch }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ch.code,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) ch.getColor() else TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name, ip, selectedChannel, platform) },
                colors = ButtonDefaults.buttonColors(containerColor = DolbyGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isBangla) "সংযুক্ত করুন" else "Connect", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel", color = TextSecondary)
            }
        }
    )
}
