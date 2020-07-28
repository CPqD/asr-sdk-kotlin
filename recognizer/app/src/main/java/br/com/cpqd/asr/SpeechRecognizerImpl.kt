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
package br.com.cpqd.asr

import android.util.AndroidRuntimeException
import android.util.Log
import br.com.cpqd.asr.audio.AudioSource
import br.com.cpqd.asr.constant.CharsetConstants.Companion.NETWORK_CHARSET
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_AUDIO_RAW
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_OCTET_STREAM
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_CREATE_SESSION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_RELEASE_SESSION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_SEND_AUDIO
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_START_RECOGNITION
import br.com.cpqd.asr.model.AsrMessage
import br.com.cpqd.asr.model.RecognitionError
import br.com.cpqd.asr.model.RecognitionResult
import br.com.cpqd.asr.model.UserAgent
import br.com.cpqd.asr.util.Util
import com.google.gson.Gson
import com.neovisionaries.ws.client.*
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread


/**
 *  Class that implements the communication with the asr server
 *
 * @property builder
 */
class SpeechRecognizerImpl(private val builder: SpeechRecognizer.Builder) :
    WebSocketListenerAsr {

    /**
     * Log tag.
     */
    private val TAG: String = SpeechRecognizerImpl::class.java.simpleName

    /**
     * Queue with the message packets to be sent to the recognition process.
     * Synchronized List
     */
    private var queue = Collections.synchronizedList(LinkedList<AsrMessage>())

    /**
     * Flag to indicate if the sending process started
     */
    private var isSending: Boolean = false

    /**
     * Flag to indicate if the last packet has been placed in the send queue
     */
    private var lastPacket = false

    /**
     * Flag to indicate if the continuous mode is enabled
     */
    private var continuousMode = false

    private val wss: WebSocket

    init {

        val factory = WebSocketFactory()
            .setConnectionTimeout(builder.maxWaitSeconds * 1000)

        factory.verifyHostname = false

        // Create the websocket connection instance
        wss = factory.createSocket(builder.uri)
            .setUserInfo(builder.credentials[0], builder.credentials[1])
            .addListener(this)
            .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)

        // Checks whether continuous mode is enabled
        isContinuousModeEnable()

        thread(start = true, name = "WebSocketConnectionThread", isDaemon = true) {
            try {
                wss.connect()
            } catch (e: OpeningHandshakeException) {
                val sl = e.statusLine
                println("=== Status Line ===")
                System.out.format("HTTP Version  = %s\n", sl.httpVersion)
                System.out.format("Status Code   = %d\n", sl.statusCode)
                System.out.format("Reason Phrase = %s\n", sl.reasonPhrase)


                val headers =
                    e.headers
                println("=== HTTP Headers ===")
                for ((name, values) in headers) {

                    if (values == null || values.size == 0) {
                        println(name)
                        continue
                    }
                    for (value in values) {
                        System.out.format("%s: %s\n", name, value)
                    }
                }

                clear()
            } catch (e: WebSocketException) {
                e.message?.let { Log.w(TAG, it) }
                clear()
            }
        }
    }


    override fun onSendingHandshake(
        websocket: WebSocket?,
        requestLine: String?,
        headers: MutableList<Array<String>>?
    ) {
        requestLine?.let {
            Log.d(TAG, requestLine)
        }
    }

    // If the connection is established, a message is sent to create the session
    override fun onConnected(
        websocket: WebSocket?,
        headers: MutableMap<String, MutableList<String>>?
    ) {
        websocket?.sendBinary(AsrMessage(METHOD_CREATE_SESSION, UserAgent.toMap()).toByteArray())
    }


    override fun onBinaryMessage(websocket: WebSocket?, binary: ByteArray?) {
        val responseMessage = AsrMessage(binary)

        // Waits for the session to be in the IDLE state to start the recognition process.
        if (responseMessage.mMethod == "RESPONSE" && responseMessage.mHeader["Session-Status"] == "IDLE") {
            websocket?.sendBinary(startRecognition())
        }

        // Waits for the session to be in the LISTENING state to start sending the audio.
        if (responseMessage.mMethod == "RESPONSE" && responseMessage.mHeader["Session-Status"] == "LISTENING") {

            // Checks whether the submission process has already started
            if (!isSending) {
                isSending = true
                thread(start = true, name = "SendAudioThread", isDaemon = true) {
                    // Stay in loop until the last packet is allocated
                    while (!lastPacket) {

                        if (queue.size > 0) {
                            if (queue.first().mHeader["LastPacket"] == "true") lastPacket = true
                            websocket?.sendBinary(queue.first().toByteArray())
                            queue.removeAt(0)
                        }
                    }
                }

            }
        }

        // Checks if the message contains any error code.
        if (responseMessage.mHeader.containsKey("Error-Code")) {

            Log.w(TAG, RecognitionError(responseMessage).toString())
            websocket?.sendBinary(AsrMessage(METHOD_RELEASE_SESSION).toByteArray())
            clear()

        }

        // Checks the status of the session and, if necessary, returns the partial response
        if (responseMessage.mMethod == "RECOGNITION_RESULT" && responseMessage.mHeader["Result-Status"] == "PROCESSING") {

            responseMessage.mBody?.toString(NETWORK_CHARSET)?.let {
                builder.recognizerPartialResult?.onPartialResultResult(
                    Gson().fromJson(
                        it,
                        RecognitionResult::class.java
                    ).getString()
                )
            }

        }


        // Check the session status and, if necessary, return the complete response
        if (responseMessage.mMethod == "RECOGNITION_RESULT" && responseMessage.mHeader["Result-Status"] == "RECOGNIZED") {

            responseMessage.mBody?.toString(NETWORK_CHARSET)?.let {
                builder.recognizerResult?.onResult(
                    Gson().fromJson(
                        it,
                        RecognitionResult::class.java
                    ).getString()
                )
            }

            // If continuous mode is disabled, the session is ended and the connection is closed
            if (!continuousMode)
                websocket?.sendBinary(AsrMessage(METHOD_RELEASE_SESSION).toByteArray())

            // If continuous mode is enabled, the session is only ended when the last packet is sent
            if (continuousMode && lastPacket)
                websocket?.sendBinary(AsrMessage(METHOD_RELEASE_SESSION).toByteArray())
        }
    }


    /**
     * Reads the Audio Source and allocates it to the queue
     *
     * @param fileAudio
     * @param contentType
     */
    fun recognizer(fileAudio: AudioSource, contentType: String) {

        if (contentType.isBlank() || !(
                    contentType.contentEquals(TYPE_OCTET_STREAM)
                            || contentType.contentEquals(
                        TYPE_AUDIO_RAW
                    ))
        ) {
            throw IllegalArgumentException()
        }

        thread(start = true, name = "AudioBufferThread", isDaemon = true) {


            // Creates the read buffer
            val readBuffer = Util.createBufferSizer(
                builder.chunkLength,
                builder.audioSampleRate,
                builder.sampleSize.getSampleSize()
            )

            var readBytes = 0

            try {

                while (readBytes != -1) {

                    // Reads the audio file and returns the number of bytes read.
                    readBytes = fileAudio.read(readBuffer)

                    // Checks if the number of bytes read is equal to the maximum size of the read buffer
                    val buffer: ByteArray = if (readBytes > 0 && readBytes != readBuffer.size) {
                        readBuffer.copyOf(readBytes)
                    } else {
                        readBuffer
                    }


                    val bufferToSend = ByteArray(buffer.size)
                    System.arraycopy(buffer, 0, bufferToSend, 0, buffer.size)

                    val message = if (readBytes > 0) {
                        AsrMessage(
                            METHOD_SEND_AUDIO,
                            mutableMapOf(
                                "LastPacket" to "false",
                                "Content-Type" to contentType
                            ),
                            bufferToSend
                        )
                    } else {
                        AsrMessage(
                            METHOD_SEND_AUDIO,
                            mutableMapOf(
                                "LastPacket" to "true",
                                "Content-Type" to contentType
                            )
                        )
                    }
                    queue.add(message)
                }

            } catch (e: IOException) {
                e.message?.let { Log.w(TAG, it) }
            } catch (e: AndroidRuntimeException) {
                e.message?.let { Log.w(TAG, it) }
            } catch (e: IllegalStateException) {
                e.message?.let { Log.w(TAG, it) }
            } finally {
                fileAudio.close()
            }
        }
    }


    private fun startRecognition(): ByteArray {
        return AsrMessage(
            METHOD_START_RECOGNITION,
            builder.recognizerConfig.configMap(),
            builder.recognizerConfigBody
        ).toByteArray()
    }

    private fun clear() {
        builder.recognizerResult?.onResult("")
    }

    private fun isContinuousModeEnable() {
        if (builder.recognizerConfig.configMap()
                .containsKey("decoder.continuousMode") && builder
                .recognizerConfig.configMap()["decoder.continuousMode"] == "true"
        )
            continuousMode = true
    }

}