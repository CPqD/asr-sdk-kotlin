package br.com.cpqd.asr.model

import android.os.Build

class UserAgent {

    companion object {


        private val model: String = Build.MODEL

        private val manufacturer: String = Build.MANUFACTURER

        private val os: String = "android"

        private val osVersion: String = Build.VERSION.RELEASE


        fun toMap(): MutableMap<String, String> {
            return mutableMapOf("User-Agent" to stringify())
        }

        private fun stringify(): String {
            return "model=$model;manufacturer=$manufacturer;os=$os;os_version=$osVersion"
        }

    }


}