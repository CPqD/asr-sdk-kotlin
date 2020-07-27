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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var toView: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        startActivity(toView)
    }

    fun onRadioButtonClicked(view: View) {
        if(view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                R.id.file -> {
                    if(checked){
                        toView = Intent(this, FileAudioActivity::class.java)
                    }
                }
                R.id.microphone -> {
                    if(checked) {
                        toView = Intent(this, MicrophoneAudioActivity::class.java)
                    }
                }
                R.id.partialResult -> {
                    if(checked) {
                        toView = Intent(this, PartialResultActivity::class.java)
                    }
                }
                R.id.continuousMode -> {
                    if(checked) {
                        toView = Intent(this, ContinuousModeActivity::class.java)
                    }
                }
            }

        }
    }
}