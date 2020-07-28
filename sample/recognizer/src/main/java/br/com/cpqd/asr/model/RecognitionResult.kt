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
package br.com.cpqd.asr.model

/**
 * Represents the final result of the recognition process.
 *
 * @property alternatives the list of recognition result alternative sentences.
 * @property segmentIndex the speech segment index.
 * @property lastSegment indicates if this is the last recognized segment.
 * @property finalResult indicates if this is the final recognized result.
 * @property startTime the audio position when the speech start was detected (in secs).
 * @property endTime the audio position when the speech stop was detected (in secs).
 * @property resultStatus the recognition result
 */
data class RecognitionResult (val alternatives : List<Alternative>, val segmentIndex: Int, val lastSegment: Boolean, val finalResult: Boolean, val startTime: Double, val endTime: Double, val resultStatus: String) {

    fun getString(): String {
        if(alternatives.isNotEmpty()){
            return alternatives.first().text
        }
        return ""
    }

}