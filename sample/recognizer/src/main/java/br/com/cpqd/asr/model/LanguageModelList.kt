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

class LanguageModelList(builder: Builder) {

    var uriList: MutableList<String>

    var grammarList: MutableList<Array<String>>

    var phraseRuleList: MutableList<String>

    init {

        uriList = builder.uriList

        grammarList = builder.grammarList

        phraseRuleList = builder.phraseRuleList

    }


    class Builder {

        internal var uriList = mutableListOf<String>()

        internal var grammarList = mutableListOf<Array<String>>()

        internal var phraseRuleList = mutableListOf<String>()


        fun build(): LanguageModelList {
            return LanguageModelList(this)
        }


        fun addFromURI(uri: String): Builder {
            uriList.add(uri)
            return this
        }


        fun addInlineGrammar(id: String, body: String): Builder {
            grammarList.add(arrayOf(id, body))
            return this
        }


        fun addPhraseRule(phrase: String) {
            phraseRuleList.add(phrase)
        }

    }


}