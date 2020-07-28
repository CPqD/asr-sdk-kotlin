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

import br.com.cpqd.asr.audio.AudioEncoding
import br.com.cpqd.asr.exception.InvalidCredentialsException
import br.com.cpqd.asr.exception.URLBlankException
import br.com.cpqd.asr.model.LanguageModelList
import br.com.cpqd.asr.model.RecognitionConfig
import java.net.URI

/**
 * Claas that
 *
 */
class SpeechRecognizer {

    class Builder {

        /**
         * ASR URI
         */
        var uri: URI? = null

        /**
         * Credentials user and password
         *
         */
        var credentials: Array<String> = arrayOf()

        /**
         * the audio sample rate.
         */
        var audioSampleRate: Int = 8000

        /**
         * the audio packets length.
         */
        var chunkLength: Int = 250

        /**
         * the timeout value (in seconds).
         */
        var maxWaitSeconds: Int = 10

        /**
         * the audio sample size in bits.
         */
        var sampleSize: AudioEncoding = AudioEncoding.LINEAR16

        /**
         * Interface for complete result
         */
        var recognizerResult: SpeechRecognizerResult? = null

        /**
         * Interface for partial result
         */
        var recognizerPartialResult: SpeechRecognizePartialResult? = null

        /**
         * Service configuration options
         */
        var recognizerConfig: RecognitionConfig = RecognitionConfig()

        /**
         *
         */
        var recognizerConfigBody: LanguageModelList? = null

        fun serverURL(url: String): Builder {
            if (url.isBlank()) {
                throw URLBlankException()
            }
            this.uri = URI(url)
            return this
        }

        fun credentials(user: String, secret: String): Builder {
            if (user.isBlank() || secret.isBlank()) {
                throw InvalidCredentialsException()
            }
            this.credentials = arrayOf(user, secret)
            return this
        }


        fun sampleRate(sampleRate : Int): Builder {
            this.audioSampleRate = sampleRate
            return this
        }

        fun chunkBuffer(chunk: Int): Builder {
            this.chunkLength = chunk
            return this
        }

        fun maxWaitSeconds(maxWaitSeconds: Int): Builder {
            this.maxWaitSeconds = maxWaitSeconds
            return this
        }

        fun sampleSize(sampleSize: AudioEncoding): Builder {
            this.sampleSize = sampleSize
            return this
        }

        fun recognizerResult(resultInterface: SpeechRecognizerResult): Builder {
            this.recognizerResult = resultInterface
            return this
        }

        fun recognizerPartialResult(partialResultInterface: SpeechRecognizePartialResult): Builder {
            this.recognizerPartialResult = partialResultInterface
            return this
        }

        fun config(config: RecognitionConfig, body: LanguageModelList): Builder {
            this.recognizerConfig = config
            this.recognizerConfigBody = body
            return this
        }

        fun build(): SpeechRecognizerImpl {
            return SpeechRecognizerImpl(this)
        }

    }

}