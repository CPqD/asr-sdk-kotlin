package br.com.cpqd.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import br.com.cpqd.asr.SpeechRecognizer
import br.com.cpqd.asr.SpeechRecognizerResult
import br.com.cpqd.asr.audio.FileAudioSource
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_JSON
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_OCTET_STREAM
import br.com.cpqd.asr.constant.ContentTypeConstants.Companion.TYPE_URI_LIST
import br.com.cpqd.asr.model.RecognitionConfig

import kotlinx.android.synthetic.main.activity_file_audio.*

class FileAudioActivity : AppCompatActivity(), View.OnClickListener, SpeechRecognizerResult {
    private var fileAudio: String = "bank_transfira_8k.wav"

    private val recognitionConfig: RecognitionConfig = RecognitionConfig.Builder()
        .accept(TYPE_JSON)
        .noInputTimeoutEnabled(true)
        .noInputTimeoutMilis(20000)
        .contentType(TYPE_URI_LIST)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_audio)

        play.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        play.isEnabled = false
        progress.show()

        val audio = FileAudioSource(applicationContext.assets.open(fileAudio))

        val speech = SpeechRecognizer.Builder()
            .serverURL("ws://10.10.0.101:8025/asr-server/asr")
            .credentials("felipe", "felipe.cpqd")
            .recognizerResult(this)
            .config(recognitionConfig, "builtin:slm/general")
            .build()

        val recognizer: Any = speech.recognizer(audio, TYPE_OCTET_STREAM)

    }


    fun onRadioButtonClicked(view: View) {

        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.id) {
                R.id.transfer -> {
                    if (checked) {
                        fileAudio = "bank_transfira_8k.wav"
                    }
                }
                R.id.bigAudio -> {
                    if (checked) {
                        fileAudio = "big_audio_8k.wav"
                    }
                }
                R.id.cpf -> {
                    if (checked) {
                        fileAudio = "cpf_8k.wav"
                    }
                }
                R.id.music -> {
                    if (checked) {
                        fileAudio = "music.wav"
                    }
                }
                R.id.noEndSilence -> {
                    if (checked) {
                        fileAudio = "no_end_silence_8k.wav"
                    }
                }
                R.id.pizza -> {
                    if (checked) {
                        fileAudio = "pizza_veg_audio_8k.wav"
                    }
                }
                R.id.silence -> {
                    if (checked) {
                        fileAudio = "silence_8k.wav"
                    }
                }
            }
        }
    }

    override fun onResult(result: String) {
        runOnUiThread {
            responseFile.text = result
            play.isEnabled = true
            progress.hide()
        }
    }
}