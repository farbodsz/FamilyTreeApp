package com.farbodsz.familytree.ui.tree

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.util.DATE_FORMATTER_LONG

/**
 * Displays a dialog providing brief details and action options for a [Person] that has been clicked
 * from a [com.farbodsz.familytree.ui.widget.TreeView].
 *
 * @see TreeActivity
 */
class PersonViewDialogFragment : DialogFragment() {

    companion object {

        const val ARGUMENT_PERSON = "arg_person"

        /**
         * Instantiates this dialog fragment with a [person] as an argument to the fragment.
         *
         * @param person                the [Person] to show in the dialog
         * @param actionChangedListener the listener implemented by the calling activity
         */
        @JvmStatic
        fun newInstance(person: Person,
                        actionChangedListener: OnDialogActionChosenListener
        ): PersonViewDialogFragment {
            val fragment = PersonViewDialogFragment()

            val args = Bundle()
            args.putParcelable(ARGUMENT_PERSON, person)

            fragment.arguments = args
            fragment.actionChosenListener = actionChangedListener

            return fragment
        }

        /**
         * An array of dialog actions that will be displayed in addition to some others.
         */
        private val STANDARD_OPTIONS_TEXT_RES = arrayOf(
                R.string.dialog_personView_item_viewPerson,
                R.string.dialog_personView_item_addParent,
                R.string.dialog_personView_item_addMarriage,
                R.string.dialog_personView_item_addChild
        )
    }

    /**
     * Functional interface used to allow the creator of this dialog fragment to run code after an
     * option in the dialog has been selected.
     */
    interface OnDialogActionChosenListener {

        /**
         * Invoked when the user has chosen to view the details of the given [person].
         */
        fun onViewPerson(person: Person)

        /**
         * Invoked when the user has chosen to add a parent to the given [person].
         */
        fun onAddParent(person: Person)

        /**
         * Invoked when the user has chosen to add a marriage involving the given [person].
         */
        fun onAddMarriage(person: Person)

        /**
         * Invoked when the user has chosen to add a child to the given [person].
         */
        fun onAddChild(person: Person)

        /**
         * Invoked when the user has chosen to view the given [person]'s tree.
         */
        fun onSwitchTree(person: Person)
    }

    private lateinit var person: Person

    /**
     * The spouses of [person].
     * This is initialised lazily and cached to avoid processing data on each access.
     */
    private val spouses by lazy { MarriagesManager(context).getSpouses(person.id) }

    /**
     * For providing a list of [people][Person] who's tree the user can choose to switch to and
     * view.
     *
     * It includes the [person] itself and his/her spouses.
     * This is initialised lazily and cached to avoid processing data on each access.
     */
    private val switchTreeOptions by lazy {
        val options = ArrayList<Person>()
        options.add(person)
        options.addAll(spouses)
        options
    }

    private lateinit var actionChosenListener: OnDialogActionChosenListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        person = arguments?.getParcelable(ARGUMENT_PERSON)
                ?: // received Person could be null, throw exception if so
                throw IllegalArgumentException("this dialog cannot display a null person")

        return AlertDialog.Builder(activity)
                .setCustomTitle(getTitleView())
                .setItems(getDialogOptions()) { _, which ->
                    invokeDialogAction(which)
                    // Dialog dismissed in onActivityResult
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
                .create()
    }

    /**
     * Returns a [View] displaying brief details of a [Person] that is used as the title of the
     * dialog.
     */
    private fun getTitleView(): View {
        val titleView = activity.layoutInflater.inflate(R.layout.item_list_person, null)

        // TODO person image
        titleView.findViewById<TextView>(R.id.text1).text = person.fullName

        val subtitle = if (spouses.isEmpty()) {
            person.dateOfBirth.format(DATE_FORMATTER_LONG) // show DOB, if no spouses
        } else {
            val aSpouse = PersonManager(context).get(spouses[0].id)
            resources.getQuantityString(
                    R.plurals.dialog_personView_marriages,
                    spouses.count(),
                    aSpouse.forename,
                    spouses.count() - 1
            )
        }

        titleView.findViewById<TextView>(R.id.text2).text = subtitle

        return titleView
    }

    /**
     * Returns a list of options for the actions that can be invoked on the dialog.
     * @see STANDARD_OPTIONS_TEXT_RES
     */
    private fun getDialogOptions(): Array<String> {
        val actions = ArrayList<String>()

        // Add standard options
        for (actionTextRes in STANDARD_OPTIONS_TEXT_RES) {
            actions.add(getString(actionTextRes))
        }

        // Add "switch to ___'s tree" options
        for (p in switchTreeOptions) {
            val actionText = getString(R.string.dialog_personView_item_switchTree, p.forename)
            actions.add(actionText)
        }

        return actions.toTypedArray()
    }

    /**
     * Invokes a particular action depending on the dialog option selected.
     *
     * @param which the index of the dialog option selected. Indices 0 to 3 inclusive correspond to
     *              the options always present, see [STANDARD_OPTIONS_TEXT_RES]. Indices of at least
     *              4 correspond to the "switch to spouse's tree" options (these are not present if
     *              the person has no spouses).
     */
    private fun invokeDialogAction(which: Int) = when (which) {
        0 -> actionChosenListener.onViewPerson(person)
        1 -> actionChosenListener.onAddParent(person)
        2 -> actionChosenListener.onAddMarriage(person)
        3 -> actionChosenListener.onAddChild(person)
        in 4..(4 + switchTreeOptions.count()) -> {
            val chosenPerson = switchTreeOptions[which - 4]
            actionChosenListener.onSwitchTree(chosenPerson)
        }
        else -> throw IllegalArgumentException("invalid index (which) value: $which")
    }

}
