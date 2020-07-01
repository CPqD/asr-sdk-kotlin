package br.com.cpqd.asr.model

import br.com.cpqd.asr.model.AsrMessage

class RecognitionError(message: AsrMessage) {

    private var errorCode: String? = null
    private var errorMessage: String? = null

    init {
        errorCode = message.mHeader["Error-Code"]
        errorMessage = message.mHeader["Message"]
    }


    override fun toString(): String {
        return "Error-Code: $errorCode - Message: $errorMessage"
    }
}