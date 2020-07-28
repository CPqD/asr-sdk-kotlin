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
package br.com.cpqd.asr.model

import android.util.Log
import br.com.cpqd.asr.constant.ByteArrayConstants.Companion.COLON
import br.com.cpqd.asr.constant.ByteArrayConstants.Companion.CRLF
import br.com.cpqd.asr.constant.ByteArrayConstants.Companion.SPACE
import br.com.cpqd.asr.constant.CharsetConstants.Companion.NETWORK_CHARSET
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.ASR_PROTOCOL
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.ASR_VERSION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_CANCEL_RECOGNITION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_CREATE_SESSION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_END_OF_SPEECH
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_RECOGNITION_RESULT
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_RELEASE_SESSION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_RESPONSE
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_SEND_AUDIO
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_START_INPUT_TIMERS
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_START_OF_SPEECH
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.METHOD_START_RECOGNITION
import br.com.cpqd.asr.constant.HeaderMethodConstants.Companion.TOKEN_REGEX
import br.com.cpqd.asr.exception.*
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * <p>Model class of the messages exchanged between client and ASR server.</p>
 * <p>A message looks roughly like this:</p>
 * <p><tt>
 * &nbsp;&nbsp;&nbsp;&nbsp;ASR 3.0 METHOD(CRLF)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;field-name: field-value(CRLF)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;(CRLF)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;(body if any)
 * </tt></p>
 */
class AsrMessage private constructor() {

    /**
     * Log tag
     */
    private val TAG: String = AsrMessage::class.java.simpleName

    /**
     * Protocol of this message.
     * It should be <tt>ASR</tt>.
     */
    private var mProtocol: String? = null
        set(value) {
            if (ASR_PROTOCOL == value) {
                field = value
            } else {
                throw WrongProtocolException("invalid message: wrong protocol - $value")
            }
        }

    /**
     * Version of the protocol of this message.
     * It should be something like <tt>2.3</tt>.
     */
    private var mVersion: String? = null
        set(value) {
            if (ASR_VERSION == value) {
                field = value
            } else {
                throw WrongVersionException("invalid message: wrong version - $value")
            }
        }

    /**
     * Method of this message.
     * It should be one of the {@code METHOD_*} values defined in this class.
     */
    var mMethod: String? = null
        set(value) {
            isValidMethodOrNotNull(value)
            field = value
        }

    /**
     * Map of header fields names and values of this message.
     */
    var mHeader = mutableMapOf<String, String>()

    /**
     * Body of this message.
     * It may be {@code null}, indicating there is no body.
     */
    var mBody: ByteArray? = null

    /**
     * Constructs an ASR message from serialized octets.
     * The ASR protocol does not define any specific character encoding,
     * so this Android library implementation reads these octets as UTF-8 encoded text.
     *
     * @param message octet-serialized ASR message.
     */
    constructor(message: ByteArray?) : this() {

        if (message == null || message.isEmpty()) {
            throw MessageEmptyException("invalid message: unexpected end of message")
        }


        // Apply the charset to the ByteArray and split it into two string
        // The first string represents the message header and the last one represent the message body
        val splitHeaderBody: List<String> = message
            .toString(NETWORK_CHARSET)
            .split("\r\n\r\n")

        // Convert the heaader into a List of String. Each line represents a parameter
        val headerLines: List<String> = splitHeaderBody[0].split("\r\n")

        val body: ByteArray = splitHeaderBody[1].toByteArray(NETWORK_CHARSET)

        // Split "ASR 2.3 METHOD" into three tokens.
        val headerFirstLine = headerLines[0].split(" ")

        // Check if message line matches "ASR 2.3 METHOD" format.
        if (headerFirstLine.size != 3) {
            throw HeaderMissingElementException("invalid message: invalid start line")
        } else {
            mProtocol = headerFirstLine[0].trim()
            mVersion = headerFirstLine[1].trim()
            mMethod = headerFirstLine[2].trim()
        }

        //Convert the header list into a Map<K,V>
        if (headerLines.size > 1) {
            mHeader = getHeaderFieldValue(headerLines)
        }


        // Valid the message body
        if (mHeader.containsKey("Content-Length")
            && !mHeader["Content-Length"].isNullOrBlank()
        ) {
            mHeader["Content-Length"]?.let {
                if (Integer.parseInt(it) == body.size) {
                    mBody = body
                } else {
                    throw BodySizeMismatchExcepetion("body size is different than the length of the content")
                }
            }
        }
    }

    /**
     * Constructs an ASR message by setting values to its variables directly.
     *
     * @param method a valid method. It should be one of the {@code METHOD_*} values defined in this class.
     * @param headerFields a map of header fields names and values.
     * @param body a payload body.
     */
    constructor(
        method: String,
        headerFields: MutableMap<String, String>,
        body: ByteArray
    ) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
        mHeader = headerFields
        mBody = body
        setHeaderBodySize()
    }

    /**
     * Constructs an ASR message by setting values to its variables directly.
     *
     * @param method a valid method. It should be one of the {@code METHOD_*} values defined in this class.
     * @param headerFields a map of header fields names and values.
     * @param lmList the language model needed for recognition
     */
    constructor(
        method: String,
        headerFields: MutableMap<String, String>,
        lmList: LanguageModelList?
    ) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
        mHeader = headerFields
        mBody = populateBody(lmList)
        setHeaderBodySize()
    }

    /**
     * Constructs an ASR message by setting values to its variables directly.
     *
     * @param method a valid method. It should be one of the {@code METHOD_*} values defined in this class.
     */
    constructor(method: String) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
    }

    /**
     * Constructs an ASR message by setting values to its variables directly.
     *
     * @param method a valid method. It should be one of the {@code METHOD_*} values defined in this class.
     * @param headerFields a map of header fields names and values.
     */
    constructor(method: String, headerFields: MutableMap<String, String>) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
        mHeader = headerFields
    }

    /**
     * Create a <k, v> map based on the header values
     * Should be like this "field-name" : "field-value"
     *
     * @param headerLines configuration header in json format
     * @return Mutable Map<String, String>
     */
    private fun getHeaderFieldValue(headerLines: List<String>): MutableMap<String, String> {
        val listFields = mutableMapOf<String, String>()

        for (x in 1 until headerLines.size) {
            if (headerLines[x].matches(("$TOKEN_REGEX:.*").toRegex())) {
                val headerFieldSplit = headerLines[x].split(":")
                listFields[headerFieldSplit[0]] = headerFieldSplit[1].trim()
            } else {
                Log.i(TAG, "ignoring invalid header field: ${headerLines[x]}")
            }
        }

        return listFields
    }

    /**
     *  Evaluates whether the given method is a valid ASR method.
     *
     * @param method the method to be evaluated.
     */
    private fun isValidMethodOrNotNull(method: String?) {

        if (method.isNullOrBlank() || !(method.contentEquals(METHOD_CANCEL_RECOGNITION)
                    || method.contentEquals(METHOD_CREATE_SESSION)
                    || method.contentEquals(METHOD_START_RECOGNITION)
                    || method.contentEquals(METHOD_SEND_AUDIO)
                    || method.contentEquals(METHOD_RELEASE_SESSION)
                    || method.contentEquals(METHOD_RECOGNITION_RESULT)
                    || method.contentEquals(METHOD_RESPONSE)
                    || method.contentEquals(METHOD_START_OF_SPEECH)
                    || method.contentEquals(METHOD_END_OF_SPEECH)
                    || method.contentEquals(METHOD_START_INPUT_TIMERS))
        ) {
            throw WrongMethodExcepetion("invalid method - $method")
        }
    }

    /**
     * Serializes this ASR message as a byte array.
     *
     * @return this ASR message serialized as a byte array.
     */
    fun toByteArray(): ByteArray {

        try {

            val baos = ByteArrayOutputStream()

            mProtocol?.let {
                baos.write(it.toByteArray(NETWORK_CHARSET))
            }

            baos.write(SPACE)

            mVersion?.let {
                baos.write(it.toByteArray(NETWORK_CHARSET))
            }

            baos.write(SPACE)

            mMethod?.let {
                baos.write(it.toByteArray(NETWORK_CHARSET))
            }

            baos.write(CRLF)


            mHeader.forEach { (key, value) ->
                baos.write(key.toByteArray(NETWORK_CHARSET))
                baos.write(COLON)
                baos.write(SPACE)
                baos.write(value.toByteArray(NETWORK_CHARSET))
                baos.write(CRLF)
            }

            baos.write(CRLF)

            mBody?.let {
                baos.write(it)
            }

            return baos.toByteArray()

        } catch (e: IOException) {
            Log.e(TAG, "could not serialize asr message into byte array", e)

            throw RuntimeException(e)
        }

    }

    /**
     * Adds the field "Content-Length" to the header based in the body size
     *
     */
    private fun setHeaderBodySize() {
        mBody?.let {
            mHeader["Content-Length"] = it.size.toString()
        }
    }

    /**
     * Populate the body with the language model
     *
     * @param lmList the language model needed for recognition
     * @return
     */
    private fun populateBody(lmList: LanguageModelList?): ByteArray? {
        val body = ByteArrayOutputStream()

        lmList?.let { languageModelList ->

            if (languageModelList.uriList.size > 0) {
                body.write(languageModelList.uriList[0].toByteArray(NETWORK_CHARSET))
            } else if (languageModelList.grammarList.size > 0) {
                val grammarList = languageModelList.grammarList[0]
                mHeader["Content-ID"] = grammarList[0]
                body.write(grammarList[1].toByteArray(NETWORK_CHARSET))
            }
        }
        return body.toByteArray()
    }


    override fun toString(): String {
        return "Message(TAG='$TAG', mProtocol=$mProtocol, mVersion=$mVersion, mMethod=$mMethod, mHeader=$mHeader, mBody=${mBody?.toString(
            NETWORK_CHARSET
        )})"
    }

}
