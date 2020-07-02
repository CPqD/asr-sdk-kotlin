package br.com.cpqd.asr.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.lang.IllegalStateException

class MicAudioSource(sampleRate: Int) : AudioSource {

    private val TAG: String = MicAudioSource::class.java.simpleName

    private val RECORDER_NUMBER_OF_CHANNELS: Int = AudioFormat.CHANNEL_IN_MONO

    private val RECORDER_AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT

    private val RECORDER_AUDIO_SOURCE: Int = MediaRecorder.AudioSource.MIC

    private var recorderSampleRate: Int = 8000

    private var recorder: AudioRecord? = null

    private var stopped: Boolean = false

    private var started: Boolean = false

    init {
        recorderSampleRate = when (sampleRate) {
            8000 -> {
                8000
            }
            16000 -> {
                16000
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        val minimumBufferSize = AudioRecord.getMinBufferSize(
            recorderSampleRate,
            RECORDER_NUMBER_OF_CHANNELS,
            RECORDER_AUDIO_FORMAT
        )

        val recorderBufferSize = when (minimumBufferSize) {
            AudioRecord.ERROR_BAD_VALUE -> {
                4096
            }
            AudioRecord.ERROR -> {
                4096
            }
            else -> {
                minimumBufferSize
            }
        }

        recorder = AudioRecord(
            RECORDER_AUDIO_SOURCE,
            recorderSampleRate,
            RECORDER_NUMBER_OF_CHANNELS,
            RECORDER_AUDIO_FORMAT,
            recorderBufferSize
        )
    }

    override fun read(byte: ByteArray): Int {
        if (stopped) return -1
        recorder?.let { audio ->
            return audio.read(byte, 0, byte.size)
        }
        return -1
    }

    fun startRecording() {
        if (!started) {
            try {
                recorder?.startRecording()
                started = true
            } catch (e: IllegalStateException) {
                e.message?.let { Log.w(TAG, it) }
            }
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            stopped = true
        } catch (e: IllegalStateException) {
            e.message?.let { Log.w(TAG, it) }
        }

    }


    override fun close() {
        stopped = true
        recorder?.stop()
        recorder?.release()

    }

    override fun finish() {
        stopRecording()
    }

}