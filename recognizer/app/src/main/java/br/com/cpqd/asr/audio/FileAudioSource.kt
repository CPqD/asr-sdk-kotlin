package br.com.cpqd.asr.audio

import br.com.cpqd.asr.audio.AudioSource
import java.io.InputStream

class FileAudioSource(private val inputStream: InputStream) : AudioSource {


    override fun read(byte: ByteArray): Int {
        return inputStream.read(byte, 0, byte.size)
    }

    override fun close() {
        inputStream.close()
    }

    override fun finish() {}

}