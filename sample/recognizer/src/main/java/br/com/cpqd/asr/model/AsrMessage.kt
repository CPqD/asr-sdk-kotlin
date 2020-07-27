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
import br.com.cpqd.asr.constant.ByteArrayContants.Companion.COLON
import br.com.cpqd.asr.constant.ByteArrayContants.Companion.CRLF
import br.com.cpqd.asr.constant.ByteArrayContants.Companion.SPACE
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


class AsrMessage private constructor() {


    private val TAG: String = AsrMessage::class.java.simpleName

    private var mProtocol: String? = null
        set(value) {
            if (ASR_PROTOCOL == value) {
                field = value
            } else {
                throw WrongProtocolException("invalid message: wrong protocol - $value")
            }
        }

    private var mVersion: String? = null
        set(value) {
            if (ASR_VERSION == value) {
                field = value
            } else {
                throw WrongVersionException("invalid message: wrong version - $value")
            }
        }

    var mMethod: String? = null
        set(value) {
            isValidMethodOrNotNull(value)
            field = value
        }

    var mHeader = mutableMapOf<String, String>()

    var mBody: ByteArray? = null

    constructor(message: ByteArray?) : this() {

        if (message == null || message.isEmpty()) {
            throw MessageEmptyException("invalid message: unexpected end of message")
        }

        /**
         * Aplica o charset UTF-8 ao ByteArray depois divide esse array em duas listas distintas
         * A primeira lista representa o cabeçalho da mensagem, enquanto a segunda lista respresenta o corpo da mensagem
         */
        val splitHeaderBody: List<String> = message
            .toString(NETWORK_CHARSET)
            .split("\r\n\r\n")

        /**
         * Divide o cabeçalho em uma nova listas
         */
        val headerLines: List<String> = splitHeaderBody[0].split("\r\n")

        val body: ByteArray = splitHeaderBody[1].toByteArray(NETWORK_CHARSET)

        /**
         * Divide a primeira linha do cabeçalho
         */
        val headerFirstLine = headerLines[0].split(" ")

        /**
         * São esperados sempre três elemenetos da primeira linha
         * ASR 2.3 START_RECOGNITION
         * Testa e verifica se existem os três elementos
         */
        if (headerFirstLine.size != 3) {
            throw HeaderMissingElementException("invalid message: invalid start line")
        } else {
            mProtocol = headerFirstLine[0].trim()
            mVersion = headerFirstLine[1].trim()
            mMethod = headerFirstLine[2].trim()
        }

        /**
         * Adiciona o resto do cabeçalho ao um Map<K,V>
         */
        if (headerLines.size > 1) {
            mHeader = getHeaderFieldValue(headerLines)
        }


        /**
         * Verifica se existe no Map do mHeader a chave "Content-Length" e caso exista a compara o com tamanho do body
         */
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


    constructor(method: String) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
    }

    constructor(method: String, headerFields: MutableMap<String, String>) : this() {
        mProtocol = ASR_PROTOCOL
        mVersion = ASR_VERSION
        mMethod = method
        mHeader = headerFields
    }


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

    private fun setHeaderBodySize() {
        mBody?.let {
            mHeader["Content-Length"] = it.size.toString()
        }
    }

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
