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
import br.com.cpqd.asr.SpeechRecognizePartialResult
import br.com.cpqd.asr.SpeechRecognizer
import br.com.cpqd.asr.SpeechRecognizerResult
import br.com.cpqd.asr.audio.MicAudioSource
import br.com.cpqd.asr.constant.ContentTypeConstants
import br.com.cpqd.asr.model.LanguageModelList
import br.com.cpqd.asr.model.RecognitionConfig
import kotlinx.android.synthetic.main.activity_partial_result.*

class PartialResultActivity : AppCompatActivity(), View.OnTouchListener, SpeechRecognizerResult,
    SpeechRecognizePartialResult {

    private val PERMISSION_REQUEST_RECORD_AUDIO: Int = 1

    private val recognitionConfig: RecognitionConfig = RecognitionConfig.Builder()
        .accept(ContentTypeConstants.TYPE_JSON)
        .contentType(ContentTypeConstants.TYPE_URI_LIST)
        .waitEndMilis(2000)
        .noInputTimeoutMilis(20000)
        .build()

    private val languageModelList: LanguageModelList = LanguageModelList.Builder()
        .addFromURI("builtin:slm/general")
        .build()


    private var audio: MicAudioSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partial_result)

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

                responseP.text = ""

                audio = MicAudioSource(8000)
                audio?.startRecording()

                val speech = SpeechRecognizer.Builder()
                    .serverURL("ws://10.10.0.112:8025/asr-server/asr")
                    .credentials("felipe", "felipe.cpqd")
                    .recognizerResult(this)
                    .recognizerPartialResult(this)
                    .config(recognitionConfig, languageModelList)
                    .build()

                audio?.let { speech.recognizer(it, ContentTypeConstants.TYPE_AUDIO_RAW) }


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
            responseC.text = result
        }
    }

    override fun onPartialResultResult(result: String) {
        runOnUiThread {
            responseP.text = result
        }
    }

}