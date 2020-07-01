package br.com.cpqd.asr.audio

import java.io.IOException

interface AudioSource {

    fun read(byte: ByteArray) : Int

    fun close()

    fun finish()
}