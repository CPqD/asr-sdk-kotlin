package br.com.cpqd.asr

import br.com.cpqd.asr.audio.AudioEncoding
import br.com.cpqd.asr.exception.InvalidCredentialsException
import br.com.cpqd.asr.exception.URLBlankException
import br.com.cpqd.asr.model.RecognitionConfig
import java.net.URI

class SpeechRecognizer {


    class Builder {

        var uri: URI? = null

        var credentials: Array<String> = arrayOf()

        var audioSampleRate: Int = 8000

        var chunkLength: Int = 250

        var maxWaitSeconds: Int = 10

        var sampleSize: AudioEncoding = AudioEncoding.LINEAR16

        var recognizerResult : SpeechRecognizerResult? = null

        var recognizerPartialResult: SpeechRecognizePartialResult? = null

        var recognizerConfig: RecognitionConfig = RecognitionConfig()

        var recognizerConfigBody: String = ""

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

        fun config(config: RecognitionConfig, body: String): Builder {
            this.recognizerConfig = config
            this.recognizerConfigBody = body
            return this
        }

        fun build(): SpeechRecognizerImpl {
            return SpeechRecognizerImpl(this)
        }

    }

}