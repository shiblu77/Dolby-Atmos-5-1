package com.example.model

import androidx.compose.ui.graphics.Color

enum class AudioChannel(
    val code: String,
    val title: String,
    val titleBn: String,
    val defaultAngleDegrees: Float,
    val frequencyPingHz: Float,
    val accentColorHex: Long
) {
    FRONT_LEFT(
        code = "FL",
        title = "Front Left",
        titleBn = "সামনে বাম (FL)",
        defaultAngleDegrees = -30f,
        frequencyPingHz = 900f,
        accentColorHex = 0xFF00E5FF
    ),
    FRONT_RIGHT(
        code = "FR",
        title = "Front Right",
        titleBn = "সামনে ডান (FR)",
        defaultAngleDegrees = 30f,
        frequencyPingHz = 1100f,
        accentColorHex = 0xFFFFC72C
    ),
    SURROUND_LEFT(
        code = "SL",
        title = "Surround Left (Rear)",
        titleBn = "পিছনে বাম (SL)",
        defaultAngleDegrees = -115f,
        frequencyPingHz = 650f,
        accentColorHex = 0xFF2979FF
    ),
    SURROUND_RIGHT(
        code = "SR",
        title = "Surround Right (Rear)",
        titleBn = "পিছনে ডান (SR)",
        defaultAngleDegrees = 115f,
        frequencyPingHz = 750f,
        accentColorHex = 0xFFFF9100
    ),
    CENTER(
        code = "C",
        title = "Center (Dialogue)",
        titleBn = "মাঝখানে / ডায়লগ (C)",
        defaultAngleDegrees = 0f,
        frequencyPingHz = 1000f,
        accentColorHex = 0xFF00E676
    ),
    SUBWOOFER(
        code = "LFE",
        title = "Subwoofer (Bass)",
        titleBn = "সাবউফার / বেস (LFE)",
        defaultAngleDegrees = 180f,
        frequencyPingHz = 70f,
        accentColorHex = 0xFFD500F9
    );

    fun getColor(): Color = Color(accentColorHex)
}

enum class DeviceRole(val label: String, val labelBn: String) {
    SATELLITE_RECEIVER("Satellite Speaker", "স্যাটেলাইট স্পিকার"),
    CINEMA_HOST("Master Hub / Host", "মাস্টার ব্রডকাস্টার"),
    LAPTOP_DESKTOP_BRIDGE("Laptop Companion", "ল্যাপটপ ব্রিজ")
}

enum class SystemPresetType(val title: String, val titleBn: String, val description: String) {
    FOUR_PHONE_ONE_LAPTOP(
        "4 Phones + 1 Laptop Cinema",
        "৪টি ফোন + ১টি ল্যাপটপ থিয়েটার",
        "Front Left, Front Right, Rear Left, Rear Right on phones; Laptop screen in center with dialogue."
    ),
    QUADRAPHONIC_SURROUND(
        "4-Corner Quad Surround (4.0)",
        "৪-কোণা কোয়াড সারাউন্ড",
        "Pure 360-degree spatial balance across 4 room corners."
    ),
    FULL_FIVE_ONE(
        "Dolby 5.1 Virtual Cinema",
        "ডলবি ৫.১ ভার্চুয়াল সিনেমা",
        "Front Left, Center, Front Right, Surround Left, Surround Right, Subwoofer."
    )
}
