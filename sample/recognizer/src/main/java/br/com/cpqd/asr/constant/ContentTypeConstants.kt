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
 * Represents the constants of the type of content necessary for communication
 *
 */
class ContentTypeConstants {
    companion object {
        const val TYPE_URI_LIST = "text/uri-list"

        const val TYPE_GRAMMMAR_XML = "application/grammar+xml"

        const val TYPE_SRGS_XML = "application/srgs+xml"

        const val TYPE_XML = "application/xml"

        const val TYPE_JSON = "application/json"

        const val TYPE_TEXT_XML = "text/xml"

        const val TYPE_SRGS = "application/srgs"

        const val TYPE_TEXT_PLAIN = "text/plain"

        const val TYPE_OCTET_STREAM = "application/octet-stream"

        const val TYPE_AUDIO_RAW = "audio/raw"

    }
}