package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AudioChannel
import com.example.model.DeviceRole
import com.example.ui.components.ChannelBadge
import com.example.ui.components.VuMeterVisualizer
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
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCalibration: () -> Unit,
    onNavigateToDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.discoveredDevices.collectAsState()
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val currentDemoTitle by viewModel.currentDemoTitle.collectAsState()
    val vuLevels by viewModel.vuMeterLevels.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val mySatelliteChannel by viewModel.mySatelliteChannel.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isBangla = userProfile.language == "বাংলা"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Cinematic Visual Asset Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.5f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.cinema_surround_hero_1794160134281),
                    contentDescription = "4-Phone + Laptop Surround Cinema Setup",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for readable text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DolbyCardDark.copy(alpha = 0.5f),
                                    DolbyCardDark.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Text overlay on banner
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DolbyGold)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DOLBY ATMOS SYNC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "৪টি ফোন + ১টি ল্যাপটপ থিয়েটার" else "4 Phones + 1 Laptop Cinema",
                            style = MaterialTheme.typography.labelMedium,
                            color = SpatialCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) "রিয়েলিস্টিক স্প্যাশিয়াল সারাউন্ড সাউন্ড" else "True Spatial Surround Immersion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device Mode Selector (Host vs Satellite Speaker)
        Card(
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "এই ডিভাইসের ভূমিকা নির্বাচন করুন:" else "Current Phone Role:",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Host Mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedRole == DeviceRole.CINEMA_HOST) DolbyGold.copy(alpha = 0.2f) else DolbyCardElevated)
                            .border(
                                width = if (selectedRole == DeviceRole.CINEMA_HOST) 2.dp else 1.dp,
                                color = if (selectedRole == DeviceRole.CINEMA_HOST) DolbyGold else DolbyBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setDeviceRole(DeviceRole.CINEMA_HOST) }
                            .padding(12.dp)
                            .testTag("role_cinema_host_btn")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cast,
                                    contentDescription = null,
                                    tint = if (selectedRole == DeviceRole.CINEMA_HOST) DolbyGold else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "মাস্টার হাব" else "Master Hub",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == DeviceRole.CINEMA_HOST) DolbyGold else TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "সাউন্ড প্লে ও সিঙ্ক নিয়ন্ত্রণ" else "Play & sync all devices",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Satellite Mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedRole == DeviceRole.SATELLITE_RECEIVER) SpatialCyan.copy(alpha = 0.2f) else DolbyCardElevated)
                            .border(
                                width = if (selectedRole == DeviceRole.SATELLITE_RECEIVER) 2.dp else 1.dp,
                                color = if (selectedRole == DeviceRole.SATELLITE_RECEIVER) SpatialCyan else DolbyBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setDeviceRole(DeviceRole.SATELLITE_RECEIVER) }
                            .padding(12.dp)
                            .testTag("role_satellite_speaker_btn")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speaker,
                                    contentDescription = null,
                                    tint = if (selectedRole == DeviceRole.SATELLITE_RECEIVER) SpatialCyan else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "স্যাটেলাইট" else "Satellite",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == DeviceRole.SATELLITE_RECEIVER) SpatialCyan else TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) "এই ফোনটি একটি স্পিকার" else "Use as 1 speaker node",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // If satellite mode, channel selector for this phone
                AnimatedVisibility(visible = selectedRole == DeviceRole.SATELLITE_RECEIVER) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = if (isBangla) "এই ফোনের জন্য চ্যানেল নির্ধারণ করুন:" else "Assign this phone's channel:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                AudioChannel.FRONT_LEFT,
                                AudioChannel.FRONT_RIGHT,
                                AudioChannel.SURROUND_LEFT,
                                AudioChannel.SURROUND_RIGHT
                            ).forEach { ch ->
                                val isSelected = mySatelliteChannel == ch
                                val chColor = ch.getColor()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) chColor.copy(alpha = 0.3f) else DolbyCardElevated)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) chColor else DolbyBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.setSatelliteChannel(ch)
                                            viewModel.pingSpeaker(ch)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ch.code,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) chColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Surround Sound Audio Player Controls
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
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = DolbyGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isBangla) "স্প্যাশিয়াল অডিও সাউন্ড ইঞ্জিন" else "Spatial Audio Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = currentDemoTitle ?: (if (isBangla) "সাউন্ড টেস্টের জন্য প্রস্তুত" else "Ready to test surround immersion"),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isPlaying) StatusGreen else TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.startCinematicSpatialDemo() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) StatusGreen else DolbyGold
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("play_spatial_demo_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) (if (isBangla) "বন্ধ করুন" else "Stop") else (if (isBangla) "৩ডি টেস্ট" else "3D Demo"),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real-Time VU Meters for all 4 surround channels + Center + Sub
                Text(
                    text = if (isBangla) "রিয়েল-টাইম চ্যানেল লেভেল ও অডিও স্পেকট্রাম" else "Real-Time Channel Signal VU Meters",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    AudioChannel.FRONT_LEFT,
                    AudioChannel.FRONT_RIGHT,
                    AudioChannel.SURROUND_LEFT,
                    AudioChannel.SURROUND_RIGHT,
                    AudioChannel.CENTER,
                    AudioChannel.SUBWOOFER
                ).chunked(2).forEach { rowChannels ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowChannels.forEach { ch ->
                            val level = vuLevels[ch] ?: 0f
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DolbyCardElevated)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ch.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ch.getColor()
                                    )
                                    Text(
                                        text = "${(level * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                VuMeterVisualizer(level = level, color = ch.getColor())
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connected Setup Architecture (4 Phones + 1 Laptop)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "সংযুক্ত ডিভাইস ক্লাস্টার (৫টি নোড)" else "Connected Cluster (${devices.size} Nodes)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            OutlinedButton(
                onClick = { onNavigateToCalibration() },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpatialCyan),
                modifier = Modifier.testTag("nav_to_calibrate_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = SpatialCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBangla) "ক্যালিব্রেট" else "Calibrate",
                    color = SpatialCyan,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        devices.forEach { dev ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.testSpeakerDevice(dev) }
                    .testTag("device_card_${dev.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChannelBadge(channel = dev.assignedChannel)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dev.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                if (dev.isLocalDevice) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DolbyGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) "এই ডিভাইস" else "This Device",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DolbyGold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${dev.platform} • ${dev.ip} • Delay: ${dev.totalDelayMs}ms (${dev.distanceMeters}m)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${dev.latencyMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Laptop Bridge Quick Launcher
        Card(
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SpatialCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToDesktop() }
                .testTag("laptop_bridge_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Laptop,
                        contentDescription = null,
                        tint = SpatialCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBangla) "ল্যাপটপ মুভি কানেক্টিভিটি ব্রিজ" else "Laptop Desktop Integration Bridge",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isBangla) "ল্যাপটপের ব্রাউজার বা স্ক্রিপ্ট থেকে অডিও স্ট্রিম করুন" else "Stream movie sound directly from Windows / Mac",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = SpatialCyan
                )
            }
        }
    }
}
