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
