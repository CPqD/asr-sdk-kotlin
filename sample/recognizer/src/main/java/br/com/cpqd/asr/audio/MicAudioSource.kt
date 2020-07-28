/*******************************************************************************
 * Copyright 2020 CPqD. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package br.com.cpqd.asr.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

/**
 * Audio source implementation for microphone input.
 *
 * @constructor
 * create an instance based on the sample rate provided
 *
 * @param sampleRate
 */

class MicAudioSource(sampleRate: Int) : AudioSource {

    /**
     * Log tag.
     */
    private val TAG: String = MicAudioSource::class.java.simpleName

    /**
     * Number of audio input channels.
     */
    private val RECORDER_NUMBER_OF_CHANNELS: Int = AudioFormat.CHANNEL_IN_MONO


    /**
     * Audio format.
     */
    private val RECORDER_AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT

    /**
     * Audio source.
     */
    private val RECORDER_AUDIO_SOURCE: Int = MediaRecorder.AudioSource.MIC

    /**
     * Default sample rate
     */
    private var recorderSampleRate: Int = 8000

    /**
     * Audio record.
     */
    private var recorder: AudioRecord? = null

    /**
     * Flag to indicate if is to stop the capture.
     */
    private var stopped: Boolean = false

    /**
     * Flag to indicate if the capture started.
     */
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