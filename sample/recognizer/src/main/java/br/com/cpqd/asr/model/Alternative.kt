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
 * Represents a alternative sentence or speech segment result of the recognition process.
 *
 * @property text the recognized text.
 * @property words the words aligment list.
 * @property score the the recognition confidence score.
 * @property lm the languagem model considered in the recognition.
 */
data class Alternative (val text: String, val words: List<Word>, val score: Int, val lm: String)