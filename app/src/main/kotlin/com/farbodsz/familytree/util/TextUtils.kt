/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farbodsz.familytree.util

/**
 * Returns a title case version of the string.
 *
 * @param delimiters    an optional array of characters that can be used to specify additional
 *                      delimiters (the boundary between two words). Whitespace characters are
 *                      already considered so they do not need to be included in this list.
 */
fun String.toTitleCase(vararg delimiters: Char): String {
    fun isDelimiter(c: Char) = Character.isWhitespace(c) || c in delimiters

    var delimiter = true
    val builder = StringBuilder(this)

    for (i in 0 until builder.length) {
        val c = builder[i]
        if (delimiter) {
            if (!isDelimiter(c)) {
                // Convert to title case and switch out of whitespace mode
                builder.setCharAt(i, Character.toTitleCase(c))
                delimiter = false
            }
        } else if (isDelimiter(c)) {
            delimiter = true
        } else {
            builder.setCharAt(i, Character.toLowerCase(c))
        }
    }

    return builder.toString()
}
