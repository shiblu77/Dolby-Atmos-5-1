package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RoomCalibrationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.discoveredDevices.collectAsState()
    val activeTestingChannel by viewModel.activeTestingChannel.collectAsState()
    val vuLevels by viewModel.vuMeterLevels.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isBangla = userProfile.language == "বাংলা"

    var selectedDeviceId by remember {
        mutableStateOf(devices.firstOrNull { it.assignedChannel == AudioChannel.FRONT_LEFT }?.id ?: devices.firstOrNull()?.id)
    }

    val selectedDevice = devices.find { it.id == selectedDeviceId } ?: devices.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "৩ডি রুম ও স্পিকার ক্যালিব্রেশন" else "3D Room & Spatial Calibration",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBangla) "স্পিকারে ট্যাপ করে সাউন্ড টেস্ট ও দূরত্ব নির্ধারণ করুন" else "Tap any speaker to ping. Calibrate speed-of-sound delays.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { viewModel.start360CalibrationSweep() },
                colors = ButtonDefaults.buttonColors(containerColor = DolbyGold),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sweep_test_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "৩৬০° সুইপ টেস্ট" else "360° Sweep",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive 2D/3D Room Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                RoomCanvasView(
                    devices = devices,
                    selectedDeviceId = selectedDevice?.id,
                    activeTestingChannel = activeTestingChannel,
                    onSelectDevice = { dev ->
                        selectedDeviceId = dev.id
                        viewModel.testSpeakerDevice(dev)
                    }
                )

                // Overlay legend
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DolbyCardElevated.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) "৪টি ফোন + ল্যাপটপ সক্রিয়" else "4 Phones + Laptop Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Speaker Calibration Panel
        if (selectedDevice != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChannelBadge(
                                channel = selectedDevice.assignedChannel,
                                isHighlighted = true
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = selectedDevice.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${selectedDevice.platform} • ${selectedDevice.ip}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.testSpeakerDevice(selectedDevice) },
                            colors = ButtonDefaults.buttonColors(containerColor = selectedDevice.assignedChannel.getColor()),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("test_selected_speaker_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "পিং টেস্ট" else "Test Ping",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Distance Slider (Acoustic Time-of-Flight calibration)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "শ্রোতা থেকে দূরত্ব (Distance)" else "Distance from Listener",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "%.1f m  (Auto Delay: %d ms)".format(selectedDevice.distanceMeters, selectedDevice.acousticDelayMs),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SpatialCyan
                        )
                    }
                    Slider(
                        value = selectedDevice.distanceMeters,
                        onValueChange = { viewModel.updateDeviceDistance(selectedDevice.id, it) },
                        valueRange = 0.5f..5.0f,
                        steps = 44,
                        colors = SliderDefaults.colors(
                            thumbColor = SpatialCyan,
                            activeTrackColor = SpatialCyan,
                            inactiveTrackColor = DolbyBorder
                        ),
                        modifier = Modifier.testTag("slider_distance_${selectedDevice.id}")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Latency Fine-tuning offset (-100ms to +100ms)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "ল্যাটেন্সি অফসেট টিউনিং" else "Sync Delay Fine-Tuning",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${if (selectedDevice.delayOffsetMs >= 0) "+" else ""}${selectedDevice.delayOffsetMs} ms",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = DolbyGold
                        )
                    }
                    Slider(
                        value = selectedDevice.delayOffsetMs.toFloat(),
                        onValueChange = { viewModel.updateDeviceDelayOffset(selectedDevice.id, it.toInt()) },
                        valueRange = -100f..100f,
                        steps = 200,
                        colors = SliderDefaults.colors(
                            thumbColor = DolbyGold,
                            activeTrackColor = DolbyGold,
                            inactiveTrackColor = DolbyBorder
                        ),
                        modifier = Modifier.testTag("slider_offset_${selectedDevice.id}")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Volume Trim
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "ভলিউম লেভেল" else "Volume Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "${(selectedDevice.volume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Slider(
                        value = selectedDevice.volume,
                        onValueChange = { viewModel.updateDeviceVolume(selectedDevice.id, it) },
                        valueRange = 0.0f..1.5f,
                        steps = 30,
                        colors = SliderDefaults.colors(
                            thumbColor = TextPrimary,
                            activeTrackColor = TextPrimary,
                            inactiveTrackColor = DolbyBorder
                        ),
                        modifier = Modifier.testTag("slider_volume_${selectedDevice.id}")
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomCanvasView(
    devices: List<SurroundDevice>,
    selectedDeviceId: String?,
    activeTestingChannel: AudioChannel?,
    onSelectDevice: (SurroundDevice) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pingRadius = remember { Animatable(0f) }

    LaunchedEffect(activeTestingChannel) {
        if (activeTestingChannel != null) {
            pingRadius.snapTo(0f)
            pingRadius.animateTo(
                targetValue = 60f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
            pingRadius.snapTo(0f)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(devices) {
                detectTapGestures { tapOffset ->
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2f
                    val centerY = height / 2f

                    // Hit test devices
                    devices.forEach { dev ->
                        val devPos = calculateDevicePosition(dev, centerX, centerY, width.toFloat(), height.toFloat())
                        val dist = (tapOffset - devPos).getDistance()
                        if (dist < 50f) {
                            onSelectDevice(dev)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Draw Room perimeter boundary
        drawRoundRect(
            color = Color(0xFF1B2130),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw acoustic range circles
        for (r in listOf(0.3f, 0.55f, 0.8f)) {
            drawCircle(
                color = Color(0xFF202738),
                radius = (size.width / 2f) * r,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Draw Center Couch / Listener Head
        drawCircle(
            color = Color(0xFF283149),
            radius = 28.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = DolbyGold,
            radius = 14.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        // Listener nose facing forward (pointing UP toward laptop/screen)
        drawLine(
            color = DolbyGold,
            start = Offset(centerX, centerY),
            end = Offset(centerX, centerY - 24.dp.toPx()),
            strokeWidth = 3.dp.toPx()
        )

        // Draw Laptop / Screen at Top Center (0°)
        val screenWidth = 70.dp.toPx()
        val screenHeight = 12.dp.toPx()
        val screenY = 28.dp.toPx()
        drawRoundRect(
            color = SpatialCyan,
            topLeft = Offset(centerX - screenWidth / 2f, screenY),
            size = androidx.compose.ui.geometry.Size(screenWidth, screenHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // Draw Devices (4 Phones placed around room)
        devices.forEach { dev ->
            val pos = calculateDevicePosition(dev, centerX, centerY, size.width, size.height)
            val isSelected = dev.id == selectedDeviceId
            val isPinging = dev.assignedChannel == activeTestingChannel
            val devColor = dev.assignedChannel.getColor()

            // Directional line from listener to speaker
            drawLine(
                color = devColor.copy(alpha = if (isSelected) 0.6f else 0.2f),
                start = Offset(centerX, centerY),
                end = pos,
                strokeWidth = if (isSelected) 2.dp.toPx() else 1.dp.toPx()
            )

            // Radiating acoustic ripple wave if pinging
            if (isPinging && pingRadius.value > 0f) {
                drawCircle(
                    color = devColor.copy(alpha = (1f - (pingRadius.value / 60f)).coerceIn(0f, 1f)),
                    radius = pingRadius.value.dp.toPx(),
                    center = pos,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Outer highlight if selected
            if (isSelected) {
                drawCircle(
                    color = devColor.copy(alpha = 0.3f),
                    radius = 24.dp.toPx(),
                    center = pos
                )
            }

            // Phone speaker icon background node
            drawCircle(
                color = if (isSelected) devColor else Color(0xFF1E2333),
                radius = 16.dp.toPx(),
                center = pos
            )
            drawCircle(
                color = devColor,
                radius = 16.dp.toPx(),
                center = pos,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun calculateDevicePosition(
    device: SurroundDevice,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
): Offset {
    val radius = (width * 0.38f) * (device.distanceMeters / 2.2f).coerceIn(0.6f, 1.15f)
    // Angles in standard room layout (0° is straight up = Laptop Screen)
    val angleRad = when (device.assignedChannel) {
        AudioChannel.FRONT_LEFT -> Math.toRadians(-40.0) // Top Left
        AudioChannel.FRONT_RIGHT -> Math.toRadians(40.0) // Top Right
        AudioChannel.SURROUND_LEFT -> Math.toRadians(-135.0) // Bottom Left
        AudioChannel.SURROUND_RIGHT -> Math.toRadians(135.0) // Bottom Right
        AudioChannel.CENTER -> Math.toRadians(0.0) // Top Center
        AudioChannel.SUBWOOFER -> Math.toRadians(180.0) // Bottom Center
    }

    // In screen coordinates: 0 rad is up (subtract PI/2 from standard polar)
    val dx = (radius * sin(angleRad)).toFloat()
    val dy = -(radius * cos(angleRad)).toFloat()
    return Offset(centerX + dx, centerY + dy)
}
