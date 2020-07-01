package br.com.cpqd.asr.audio

enum class AudioEncoding(private val sampleSize: Int) {

    LINEAR16(16),
    LINEAER8(8);

    fun getSampleSize(): Int {
        return sampleSize
    }

}