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
package br.com.cpqd.asr.constant

/**
 * Represent the constants avaliable in the header of the asr message
 *
 */
class HeaderMethodConstants {
    companion object {

        const val ASR_PROTOCOL: String = "ASR"

        const val ASR_MAJOR_VERSION: String = "2"

        const val ASR_MINOR_VERSION: String = "3"

        const val ASR_VERSION: String = "$ASR_MAJOR_VERSION.$ASR_MINOR_VERSION"

        const val METHOD_CANCEL_RECOGNITION: String = "CANCEL_RECOGNITION"

        const val METHOD_CREATE_SESSION: String = "CREATE_SESSION"

        const val METHOD_START_RECOGNITION: String = "START_RECOGNITION"

        const val METHOD_SEND_AUDIO: String = "SEND_AUDIO"

        const val METHOD_RELEASE_SESSION: String = "RELEASE_SESSION"

        const val METHOD_RECOGNITION_RESULT: String = "RECOGNITION_RESULT"

        const val METHOD_RESPONSE: String = "RESPONSE"

        const val METHOD_START_OF_SPEECH: String = "START_OF_SPEECH"

        const val METHOD_END_OF_SPEECH: String = "END_OF_SPEECH"

        const val METHOD_START_INPUT_TIMERS = "START_INPUT_TIMERS"

        const val TOKEN_REGEX: String = "[\\Q!#$%&'*+-.^_`|~\\E0-9A-Za-z]+"

    }
}