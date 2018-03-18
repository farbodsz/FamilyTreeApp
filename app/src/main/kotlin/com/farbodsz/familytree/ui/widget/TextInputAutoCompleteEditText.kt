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

package com.farbodsz.familytree.ui.widget

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * A subclass of [AppCompatAutoCompleteTextView] designed for use as a child of
 * [android.support.design.widget.TextInputLayout].
 *
 * Essentially this provides features of both [android.widget.AutoCompleteTextView] and
 * [android.support.design.widget.TextInputEditText] since there is no built-in Android widget which
 * does this.
 */
open class TextInputAutoCompleteEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatAutoCompleteTextView(context, attrs, defStyle) {

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        val ic = super.onCreateInputConnection(outAttrs)
        if (ic != null && outAttrs?.hintText == null) {
            // If we don't have a hint and our parent is a TextInputLayout, use it's hint for the
            // EditorInfo. This allows us to display a hint in 'extract mode'.
            val parent = parent
            if (parent is TextInputLayout) {
                outAttrs?.hintText = parent.hint
            }
        }
        return ic
    }

}
