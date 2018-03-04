package co.familytreeapp.ui.widget

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
