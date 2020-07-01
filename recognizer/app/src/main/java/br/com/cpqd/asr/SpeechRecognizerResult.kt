package br.com.cpqd.asr

interface SpeechRecognizerResult {
    fun onResult(result: String)
}