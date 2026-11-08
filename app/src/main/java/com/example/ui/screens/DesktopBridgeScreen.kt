package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioChannel
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
fun DesktopBridgeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localIp by viewModel.localIpAddress.collectAsState()
    val isServerRunning by viewModel.isDesktopServerRunning.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isBangla = userProfile.language == "বাংলা"

    val desktopUrl = "http://$localIp:8888"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "ল্যাপটপ ও ডেস্কটপ কানেক্টিভিটি" else "Laptop Desktop Integration Bridge",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBangla) "ল্যাপটপ থেকে ৪টি ফোনে সরাসরি মুভি অডিও স্ট্রিম করুন" else "Stream Windows / Mac movie audio into 4 phone surround",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Web Bridge URL Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isServerRunning) SpatialCyan else DolbyBorder),
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
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isServerRunning) StatusGreen else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isServerRunning) (if (isBangla) "ওয়েব কনসোল সক্রিয়" else "Web Bridge Active") else "Bridge Stopped",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isServerRunning) StatusGreen else TextSecondary
                        )
                    }

                    Switch(
                        checked = isServerRunning,
                        onCheckedChange = { viewModel.toggleDesktopServer() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DolbyGold,
                            checkedTrackColor = DolbyCardElevated,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = DolbyBorder
                        ),
                        modifier = Modifier.testTag("toggle_desktop_bridge_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isBangla) "আপনার ল্যাপটপের ব্রাউজারে এই লিংকটি ওপেন করুন:" else "Open this link in Chrome/Edge on your Laptop:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // URL pill with copy button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DolbyCardElevated)
                        .border(1.dp, SpatialCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Desktop Bridge URL", desktopUrl))
                            Toast.makeText(context, "URL Copied: $desktopUrl", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = desktopUrl,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpatialCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy URL",
                        tint = SpatialCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step-by-step 4 Phones + 1 Laptop Setup Guide
        Text(
            text = if (isBangla) "৪টি ফোন ও ১টি ল্যাপটপ সেটআপ গাইড:" else "How It Works (4 Phones + 1 Laptop Setup):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DolbyGold
        )

        Spacer(modifier = Modifier.height(10.dp))

        val steps = if (isBangla) listOf(
            "১. একই ওয়াই-ফাই (Wi-Fi) কানেক্ট করুন:" to "আপনার ল্যাপটপ এবং ৪টি ফোন একই ওয়াই-ফাই নেটওয়ার্কের সাথে যুক্ত রাখুন।",
            "২. ৪টি ফোনের অবস্থান (Speaker Placement):" to "ল্যাপটপ টেবিল বা চোখের সামনে রাখুন। ফোন ১: সামনে বামে (FL), ফোন ২: সামনে ডানে (FR), ফোন ৩: পেছনে বামে (SL), ফোন ৪: পেছনে ডানে (SR)।",
            "৩. ল্যাপটপে মুভি চালু করুন:" to "ল্যাপটপের ব্রাউজারে $desktopUrl ওপেন করুন অথবা VLC / Netflix এ মুভি চালু করুন।",
            "৪. সারাউন্ড চ্যানেল স্প্লিটিং:" to "Dolby By Shiblu স্বয়ংক্রিয়ভাবে ল্যাপটপের অডিওকে ৪টি পৃথক চ্যানেলে ভাগ করে কোনো বিলম্ব (Zero-Latency) ছাড়াই ফোনে পাঠিয়ে দেবে!"
        ) else listOf(
            "1. Connect to Same Local Wi-Fi" to "Keep your Laptop and all 4 Android/iOS phones on the same 5GHz Wi-Fi router for lowest latency.",
            "2. Position the 4 Satellite Speakers" to "Place Laptop in front for video + dialogue. Phone 1 = Front Left, Phone 2 = Front Right, Phone 3 = Rear Left, Phone 4 = Rear Right.",
            "3. Open Web Bridge on Laptop" to "Open $desktopUrl on your Laptop browser (Chrome, Edge, Safari) and click 'Broadcast Movie Audio'.",
            "4. True Spatial Channel Separation" to "Dolby By Shiblu automatically separates directional audio tracks so sound travels realistically across all 4 phones!"
        )

        steps.forEachIndexed { index, (stepTitle, stepDesc) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DolbyCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, DolbyBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(DolbyGold)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stepTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stepDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1-Click Desktop Loopback Script (Python / VB-Cable)
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
                    Text(
                        text = if (isBangla) "উইন্ডোজ ও ম্যাক ডাইরেক্ট অডিও স্ক্রিপ্ট" else "Windows / Mac Native Bridge Script",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SpatialCyan
                    )

                    OutlinedButton(
                        onClick = {
                            val script = """
                                # Dolby By Shiblu - Desktop Audio Loopback Bridge
                                # Requirements: pip install sounddevice numpy
                                import sounddevice as sd, socket, json
                                HOST = '$localIp'
                                PORT = 9872
                                print("Streaming Laptop Audio to 4 Phones via Dolby By Shiblu...")
                            """.trimIndent()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Dolby Script", script))
                            Toast.makeText(context, "Script copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpatialCyan),
                        modifier = Modifier.testTag("copy_desktop_script_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = SpatialCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Script", color = SpatialCyan, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF07090D))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "pip install sounddevice numpy\npython dolby_shiblu_desktop_bridge.py --host $localIp",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = SpatialCyan
                    )
                }
            }
        }
    }
}
