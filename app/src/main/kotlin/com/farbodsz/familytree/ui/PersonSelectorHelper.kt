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

package com.farbodsz.familytree.ui

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.person.PersonAdapter

/**
 * A helper class to display a [Person] on a [TextInputEditText], and allowing it to be changed
 * through click then dialog.
 *
 * @property context            context from the activity/fragment
 * @property textInputEditText  the [TextInputEditText] being used for the person picker
 */
class PersonSelectorHelper(
        private val context: Context,
        private val textInputEditText: TextInputEditText
) {

    /**
     * The [Person] being displayed. This is null if no person has been selected.
     */
    var person: Person? = null
        set(value) {
            field = value
            textInputEditText.setText(value?.fullName)
        }

    /**
     * Function to be invoked when the user clicks the "Create new" button in the dialog.
     */
    var onCreateNewPerson: ((dialog: DialogInterface, which: Int) -> Unit)? = null
        set(value) {
            field = value
            setupOnClickListener()
        }

    /**
     * Disables a reaction to the selector ([textInputEditText]) being clicked.
     * This prevents the [person] selector dialog being shown (so a [person] cannot be changed).
     *
     * By default, onClick is enabled.
     */
    var onClickEnabled: Boolean = true
        set(value) {
            field = value
            if (field) {
                setupOnClickListener() // enabled
            } else {
                textInputEditText.setOnClickListener(null) // disabled
            }
        }

    init {
        textInputEditText.isFocusableInTouchMode = false
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        if (!onClickEnabled) {
            textInputEditText.setOnClickListener(null)
            return
        }

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.dialog_choose_person_title)
                .setNegativeButton(android.R.string.cancel) { _, _ ->  }

        onCreateNewPerson?.let {
            builder.setPositiveButton(R.string.action_create_new) { dialog, which ->
                it.invoke(dialog, which)
            }
        }

        val dialog = builder.create()
        dialog.setView(createPersonSelector(dialog))

        textInputEditText.setOnClickListener { dialog.show() }
    }

    /**
     * Returns the [View] used for the displaying a list of all [people][Person].
     */
    private fun createPersonSelector(dialog: AlertDialog): RecyclerView {
        val personAdapter = PersonAdapter(PersonManager(context).getAll())
        personAdapter.onItemClick { _, newPerson ->
            person = newPerson
            dialog.dismiss()
        }

        return RecyclerView(context).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

}
