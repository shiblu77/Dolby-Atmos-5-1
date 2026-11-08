package com.example

import com.example.model.AudioChannel
import com.example.model.SurroundDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAcousticSpeedOfSoundDelayCalculation() {
        val device = SurroundDevice(
            id = "phone_fl",
            name = "Phone Front Left",
            ip = "192.168.1.101",
            assignedChannel = AudioChannel.FRONT_LEFT,
            distanceMeters = 1.715f, // 1.715m / 0.343 m/ms = 5 ms
            delayOffsetMs = 2
        )
        // 1.715 / 0.343 = 5 ms
        assertEquals(5, device.acousticDelayMs)
        // Total delay = 5 + 2 = 7 ms
        assertEquals(7, device.totalDelayMs)
    }

    @Test
    fun testAudioChannelsExist() {
        val channels = AudioChannel.entries
        assertTrue(channels.contains(AudioChannel.FRONT_LEFT))
        assertTrue(channels.contains(AudioChannel.FRONT_RIGHT))
        assertTrue(channels.contains(AudioChannel.SURROUND_LEFT))
        assertTrue(channels.contains(AudioChannel.SURROUND_RIGHT))
        assertTrue(channels.contains(AudioChannel.CENTER))
        assertTrue(channels.contains(AudioChannel.SUBWOOFER))
    }
}
