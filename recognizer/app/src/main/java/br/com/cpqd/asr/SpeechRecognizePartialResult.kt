package br.com.cpqd.asr

interface SpeechRecognizePartialResult {
    fun onPartialResultResult(result: String)
}