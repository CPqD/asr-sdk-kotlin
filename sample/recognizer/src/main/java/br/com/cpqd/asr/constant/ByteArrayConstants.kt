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

import br.com.cpqd.asr.constant.CharsetConstants.Companion.NETWORK_CHARSET

/**
 * Represents the constants used for the creation the array
 *
 */
class ByteArrayConstants {
    companion object {

        /**
         * UTF-8 (hex) 0x20
         */
        val SPACE: ByteArray = " ".toByteArray(NETWORK_CHARSET)

        /**
         * UTF-8 (hex) 0x0D 0x0A
         */
        val CRLF: ByteArray = "\r\n".toByteArray(NETWORK_CHARSET)

        /**
         * UTF-8 (hex) 0x3A
         */
        val COLON: ByteArray = ":".toByteArray(NETWORK_CHARSET)

    }
}