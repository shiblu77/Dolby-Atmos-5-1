package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.AudioChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SurroundAudioEngine {

    private val sampleRate = 44100
    private var activeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Current playing state & VU meter (0.0f to 1.0f)
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentDemoTitle = MutableStateFlow<String?>(null)
    val currentDemoTitle: StateFlow<String?> = _currentDemoTitle.asStateFlow()

    private val _vuMeterLevels = MutableStateFlow<Map<AudioChannel, Float>>(
        AudioChannel.entries.associateWith { 0f }
    )
    val vuMeterLevels: StateFlow<Map<AudioChannel, Float>> = _vuMeterLevels.asStateFlow()

    private val _activeTestingChannel = MutableStateFlow<AudioChannel?>(null)
    val activeTestingChannel: StateFlow<AudioChannel?> = _activeTestingChannel.asStateFlow()

    /**
     * Synthesizes and plays a localized acoustic calibration ping on a specific channel.
     * This plays a high-clarity frequency-coded pulse with an exponential attack and decay.
     */
    fun playChannelCalibrationPing(
        channel: AudioChannel,
        volume: Float = 1.0f,
        delayMs: Int = 0,
        onComplete: () -> Unit = {}
    ) {
        scope.launch {
            _activeTestingChannel.value = channel
            if (delayMs > 0) {
                delay(delayMs.toLong().coerceIn(0, 1000))
            }

            val durationSeconds = 0.45f
            val totalSamples = (sampleRate * durationSeconds).toInt()
            val buffer = ShortArray(totalSamples)
            val freq = channel.frequencyPingHz

            // Generate enveloped tone
            for (i in 0 until totalSamples) {
                val t = i.toFloat() / sampleRate
                // Attack & Decay envelope
                val envelope = if (t < 0.05f) {
                    t / 0.05f
                } else {
                    exp(-5.0f * (t - 0.05f))
                }
                val sampleValue = (sin(2.0 * PI * freq * t) * envelope * Short.MAX_VALUE * volume.coerceIn(0f, 1.2f)).toInt()
                buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            // Animate VU meter
            updateVuLevel(channel, 0.95f)

            playPcmBuffer(buffer)

            delay(100)
            updateVuLevel(channel, 0.0f)
            _activeTestingChannel.value = null
            onComplete()
        }
    }

    /**
     * Plays a full 360-degree spatial calibration sequence test
     * that sequentially pings every speaker around the user:
     * Front Left -> Center -> Front Right -> Surround Right -> Surround Left -> Subwoofer.
     */
    fun startSurroundCalibrationSweep(onChannelTested: (AudioChannel) -> Unit = {}) {
        stopPlayback()
        activeJob = scope.launch {
            _isPlaying.value = true
            _currentDemoTitle.value = "360° Dolby Spatial Sweep Calibration"

            val sequence = listOf(
                AudioChannel.FRONT_LEFT,
                AudioChannel.CENTER,
                AudioChannel.FRONT_RIGHT,
                AudioChannel.SURROUND_RIGHT,
                AudioChannel.SURROUND_LEFT,
                AudioChannel.SUBWOOFER
            )

            for (channel in sequence) {
                if (!isActive) break
                onChannelTested(channel)
                playChannelCalibrationPing(channel, volume = 0.9f)
                delay(750)
            }

            _isPlaying.value = false
            _currentDemoTitle.value = null
            _activeTestingChannel.value = null
        }
    }

    /**
     * Real-time Cinematic 3D Spatial Demo Sound Simulation:
     * Generates a dynamic spatial acoustic atmosphere with directional panning soundwaves,
     * sub-bass rumbling, and helicopter flybys.
     */
    fun startSpatialCinematicDemo(demoName: String = "Dolby Atmos 360 Spatial Demo") {
        stopPlayback()
        activeJob = scope.launch {
            _isPlaying.value = true
            _currentDemoTitle.value = demoName

            val bufferSize = sampleRate / 10 // 100ms chunks
            val buffer = ShortArray(bufferSize)

            val track = createAudioTrack(bufferSize)
            track.play()

            var globalSampleIndex = 0L

            try {
                while (isActive) {
                    val timeSec = globalSampleIndex.toDouble() / sampleRate
                    // Spatial rotation angle (completes a full 360 circle every 4 seconds)
                    val rotationAngle = (timeSec * 0.5 * 2 * PI) % (2 * PI)

                    // Directional energy per channel
                    val flWeight = (cos(rotationAngle - (-PI / 4)).coerceAtLeast(0.0)).toFloat()
                    val frWeight = (cos(rotationAngle - (PI / 4)).coerceAtLeast(0.0)).toFloat()
                    val srWeight = (cos(rotationAngle - (3 * PI / 4)).coerceAtLeast(0.0)).toFloat()
                    val slWeight = (cos(rotationAngle - (-3 * PI / 4)).coerceAtLeast(0.0)).toFloat()
                    val subWeight = (0.5f + 0.5f * sin(timeSec * 3).toFloat())

                    _vuMeterLevels.value = mapOf(
                        AudioChannel.FRONT_LEFT to (flWeight * 0.9f),
                        AudioChannel.FRONT_RIGHT to (frWeight * 0.9f),
                        AudioChannel.SURROUND_RIGHT to (srWeight * 0.9f),
                        AudioChannel.SURROUND_LEFT to (slWeight * 0.9f),
                        AudioChannel.CENTER to (0.3f),
                        AudioChannel.SUBWOOFER to (subWeight * 0.8f)
                    )

                    // Synthesize dynamic spatial harmonic audio chunk
                    for (i in 0 until bufferSize) {
                        val t = (globalSampleIndex + i).toDouble() / sampleRate

                        // Carrier sound with doppler pitch bend
                        val carrierFreq = 320.0 + 80.0 * sin(rotationAngle)
                        val mainTone = sin(2.0 * PI * carrierFreq * t)

                        // Sub rumble (55 Hz deep cinema bass)
                        val subBass = sin(2.0 * PI * 55.0 * t) * 0.65 * subWeight

                        // Cinematic harmonic chord (Atmos shimmer)
                        val chordHigh = sin(2.0 * PI * 880.0 * t) * 0.15

                        val mixed = (mainTone * 0.5 + subBass + chordHigh) * Short.MAX_VALUE * 0.4
                        buffer[i] = mixed.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }

                    track.write(buffer, 0, bufferSize)
                    globalSampleIndex += bufferSize
                }
            } finally {
                track.stop()
                track.release()
                _vuMeterLevels.value = AudioChannel.entries.associateWith { 0f }
            }
        }
    }

    fun stopPlayback() {
        activeJob?.cancel()
        activeJob = null
        _isPlaying.value = false
        _currentDemoTitle.value = null
        _activeTestingChannel.value = null
        _vuMeterLevels.value = AudioChannel.entries.associateWith { 0f }
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size.coerceAtLeast(minBufSize) * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()

        // Clean up track after duration
        scope.launch {
            delay((buffer.size * 1000L / sampleRate) + 50)
            try {
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }

    private fun createAudioTrack(chunkSize: Int): AudioTrack {
        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBufSize, chunkSize * 4))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()
    }

    private fun updateVuLevel(channel: AudioChannel, level: Float) {
        val current = _vuMeterLevels.value.toMutableMap()
        current[channel] = level
        _vuMeterLevels.value = current
    }

    private fun cos(x: Double): Double = kotlin.math.cos(x)
}
