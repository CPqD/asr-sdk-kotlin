package br.com.cpqd.asr

import br.com.cpqd.asr.exception.InvalidCredentialsException
import br.com.cpqd.asr.exception.URLBlankException
import org.junit.Test
import java.net.URISyntaxException

class SpeechRecognizerTest {

    @Test(expected = URLBlankException::class)
    fun should_ThrowException_WhenURLIsBlank(){
        SpeechRecognizer
            .Builder()
            .serverURL("")
    }

    @Test(expected = URISyntaxException::class)
    fun should_ThrowException_WhenStringCouldNotBeParsedAsURI(){
        SpeechRecognizer
            .Builder()
            .serverURL("wss: //speech. cpqd.com .br/asr/ws/v2/ recognize /8k")
    }

    @Test
    fun testAsrURL() {
        SpeechRecognizer.Builder().serverURL("wss://speech.cpqd.com.br/asr/ws/v2/recognize/8k")
    }

    @Test(expected = InvalidCredentialsException::class)
    fun should_ThrowException_WhenCredentialsAreBlank(){
        SpeechRecognizer
            .Builder()
            .serverURL("wss://speech.cpqd.com.br/asr/ws/v2/recognize/8k")
            .credentials("", "")
    }


}