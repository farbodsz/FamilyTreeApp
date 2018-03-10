package com.farbodsz.familytree.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.PersonManager

/**
 * A [TextInputAutoCompleteEditText] which provides a list of surname suggestions.
 *
 * @see com.farbodsz.familytree.model.Person
 */
class SurnameAutoCompleteEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : TextInputAutoCompleteEditText(context, attrs, defStyle) {

    init {
        threshold = 2 // start displaying suggestions after 2 characters
        setAdapter(makeAdapter())
    }

    /**
     * Returns an [ArrayAdapter] that can be used to provide the list and layout of suggestions.
     */
    private fun makeAdapter() =
            ArrayAdapter<String>(context, R.layout.item_text_dropdown, getSurnames())

    /**
     * Returns a list of surnames that can be used as suggestions.
     */
    private fun getSurnames(): List<String> {
        val surnames = ArrayList<String>()
        val people = PersonManager(context).getAll()
        for (p in people) {
            if (p.surname !in surnames) { // don't add duplicates
                surnames.add(p.surname)
            }
        }
        return surnames
    }

}
