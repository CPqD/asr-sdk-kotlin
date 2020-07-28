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
 * Represents the language models used during the speech recognition process.
 *
 * @constructor
 * TODO
 *
 * @param builder
 */
class LanguageModelList(builder: Builder) {

    /**
     * the language model URI list.
     */
    var uriList: MutableList<String>

    /**
     * the inline grammar body list.
     */
    var grammarList: MutableList<Array<String>>

    /**
     * the phrase rule list.
     */
    var phraseRuleList: MutableList<String>


    init {

        uriList = builder.uriList

        grammarList = builder.grammarList

        phraseRuleList = builder.phraseRuleList

    }

    /**
     * The Builder object.
     *
     */
    class Builder {

        internal var uriList = mutableListOf<String>()

        internal var grammarList = mutableListOf<Array<String>>()

        internal var phraseRuleList = mutableListOf<String>()


        /**
         * Creates a new instance of the object builder.
         *
         * @return the Builder object.
         */
        fun build(): LanguageModelList {
            return LanguageModelList(this)
        }

        /**
         * Adds a new language model from its URI.
         *
         * @param uri the languagem model URI.
         * @return the builder object.
         */
        fun addFromURI(uri: String): Builder {
            uriList.add(uri)
            return this
        }

        /**
         * Adds a new grammar content.
         *
         * @param id the grammar identification.
         * @param body the grammar body content.
         * @return the builder object.
         */
        fun addInlineGrammar(id: String, body: String): Builder {
            grammarList.add(arrayOf(id, body))
            return this
        }

        /**
         * Adds a new phrase rule.
         *
         * @param phrase the phrase rule.
         * @return the builder object.
         */
        fun addPhraseRule(phrase: String): Builder {
            phraseRuleList.add(phrase)
            return this
        }

    }


}