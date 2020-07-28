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
package br.com.cpqd.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.cpqd.asr.SpeechRecognizer
import br.com.cpqd.asr.SpeechRecognizerResult
import br.com.cpqd.asr.audio.MicAudioSource
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_AUDIO_RAW
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_JSON
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_URI_LIST
import br.com.cpqd.asr.model.LanguageModelList
import br.com.cpqd.asr.model.RecognitionConfig
import kotlinx.android.synthetic.main.activity_microphone_audio.*

class MicrophoneAudioActivity : AppCompatActivity(), View.OnTouchListener, SpeechRecognizerResult {

    private val PERMISSION_REQUEST_RECORD_AUDIO: Int = 1


    private val recognitionConfig: RecognitionConfig = RecognitionConfig.Builder()
        .accept(TYPE_JSON)
        .contentType(TYPE_URI_LIST)
        .waitEndMilis(2000)
        .noInputTimeoutMilis(20000)
        .build()

    private val languageModelList: LanguageModelList = LanguageModelList.Builder()
        .addFromURI("builtin:slm/general")
        .build()


    private var audio: MicAudioSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_microphone_audio)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
        }

        playMic.setOnTouchListener(this)

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Toast.makeText(this, "Iniciando...", Toast.LENGTH_SHORT).show()

                audio = MicAudioSource(8000)
                audio?.startRecording()

                val speech = SpeechRecognizer.Builder()
                    .serverURL("wss://speech.cpqd.com.br/asr/ws/v2/recognize/8k")
                    .credentials("felipe", "felipe.cpqd")
                    .recognizerResult(this)
                    .config(recognitionConfig, languageModelList)
                    .build()

                audio?.let { speech.recognizer(it, TYPE_AUDIO_RAW) }

                return true
            }

            MotionEvent.ACTION_UP -> {
                Toast.makeText(this, "Finalizando...", Toast.LENGTH_SHORT).show()
                audio?.stopRecording()

                return true
            }
        }
        return false
    }

    override fun onResult(result: String) {
        runOnUiThread {
            responseMic.text = result
        }
    }

}