package co.familytreeapp.ui.widget

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import co.familytreeapp.R
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Person
import co.familytreeapp.ui.adapter.PersonAdapter

/**
 * A helper class to display a [Person] on a [TextInputEditText], and allowing it to be changed
 * through click then dialog.
 *
 * @param context           context from the activity/fragment
 * @param textInputEditText the [TextInputEditText] being used for the person picker
 * @param initialPerson     initial [person] used for this [PersonSelectorHelper]. It can be null
 *                          (its default value), to indicate no initial person. It can be changed
 *                          later by the [person] field.
 */
class PersonSelectorHelper(
        private val context: Context,
        private val textInputEditText: TextInputEditText,
        initialPerson: Person? = null
) {

    /**
     * The [Person] being displayed. This is null if no person has been selected.
     */
    var person = initialPerson
        set(value) {
            field = value
            textInputEditText.setText(value?.fullName)
        }

    /**
     * Function for extra actions that can be invoked after the user chooses (or changed) a person.
     */
    var onPersonSet: ((view: View, newPerson: Person) -> Unit)? = null

    init {
        textInputEditText.isFocusableInTouchMode = false
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.dialog_choose_person_title)
                .setView(createPersonSelector())
                .setNegativeButton(android.R.string.cancel) { _, _ ->  }
                .create()

        textInputEditText.setOnClickListener { dialog.show() }
    }

    /**
     * Returns the [View] used for the displaying a list of all [people][Person].
     */
    private fun createPersonSelector(): RecyclerView {
        val personAdapter = PersonAdapter(context, PersonManager(context).getAll())
        personAdapter.onItemClick { view, newPerson ->
            person = newPerson
            onPersonSet?.invoke(view, newPerson)
        }

        return RecyclerView(context).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

}
