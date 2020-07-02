package br.com.cpqd.sample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.cpqd.asr.SpeechRecognizer
import br.com.cpqd.asr.SpeechRecognizerResult
import br.com.cpqd.asr.audio.MicAudioSource
import br.com.cpqd.asr.constant.ContentTypeConstants
import br.com.cpqd.asr.model.RecognitionConfig
import kotlinx.android.synthetic.main.activity_continuous_mode.*

class ContinuousModeActivity : AppCompatActivity(), View.OnTouchListener, SpeechRecognizerResult {

    private val PERMISSION_REQUEST_RECORD_AUDIO: Int = 1


    private val recognitionConfig: RecognitionConfig = RecognitionConfig.Builder()
        .accept(ContentTypeConstants.TYPE_JSON)
        .contentType(ContentTypeConstants.TYPE_URI_LIST)
        .waitEndMilis(2000)
        .noInputTimeoutMilis(20000)
        .continuousMode(true)
        .build()


    private var audio: MicAudioSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continuous_mode)

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
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Toast.makeText(this, "Iniciando...", Toast.LENGTH_SHORT).show()

                audio = MicAudioSource(8000)
                audio?.startRecording()

                val speech = SpeechRecognizer.Builder()
                    .serverURL("ws://10.10.0.101:8025/asr-server/asr")
                    .credentials("felipe", "felipe.cpqd")
                    .recognizerResult(this)
                    .config(recognitionConfig, "builtin:slm/general")
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
        responseContinuous.text = result
    }


}