package com.farbodsz.familytree.ui.tree

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.marriage.EditMarriageActivity
import com.farbodsz.familytree.ui.person.CreatePersonActivity
import com.farbodsz.familytree.ui.person.ViewPersonActivity
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH

/**
 * Displays a dialog providing brief details and action options for a [Person] that has been clicked
 * from a [com.farbodsz.familytree.ui.widget.TreeView].
 *
 * @see TreeActivity
 */
class PersonViewDialogFragment : DialogFragment() {

    private lateinit var person: Person

    /**
     * The spouses of [person].
     * This is initialised lazily and cached to avoid processing data on each access.
     */
    private val spouses by lazy {
        MarriagesManager(context).getSpouses(person.id)
    }

    companion object {

        const val ARGUMENT_PERSON = "arg_person"

        /**
         * Instantiates this dialog fragment with a [person] as an argument.
         */
        @JvmStatic
        fun newInstance(person: Person): PersonViewDialogFragment {
            val fragment = PersonViewDialogFragment()

            val args = Bundle()
            args.putParcelable(ARGUMENT_PERSON, person)
            fragment.arguments = args

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

        internal const val REQUEST_VIEW_PERSON = 11
        internal const val REQUEST_ADD_PARENT = 12
        internal const val REQUEST_ADD_MARRIAGE = 13
        internal const val REQUEST_ADD_CHILD = 14
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        person = arguments?.getParcelable(ARGUMENT_PERSON)
                ?: // received Person could be null, throw exception if so
                throw IllegalArgumentException("this dialog cannot display a null person")

        return AlertDialog.Builder(activity)
                .setCustomTitle(getTitleView())
                .setItems(getDialogOptions()) { _, which ->
                    invokeDialogAction(which)
                    dismiss()
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
            person.dateOfBirth.format(DATE_FORMATTER_BIRTH) // show DOB, if no spouses
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
        for (spouse in spouses) {
            val actionText = getString(R.string.dialog_personView_item_switchTree, spouse.forename)
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
        0 -> viewPerson()
        1 -> addParent()
        2 -> addMarriage()
        3 -> addChild()
        in 4..(4 + spouses.count()) -> switchTreeTo(spouses[which - 4])
        else -> throw IllegalArgumentException("invalid index (which) value: $which")
    }

    private fun viewPerson() {
        val intent = Intent(activity, ViewPersonActivity::class.java)
                .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
        startActivityForResult(intent, REQUEST_VIEW_PERSON)
    }

    private fun addParent() {
        val intent = Intent(activity, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_PARENT)
    }

    private fun addMarriage() {
        val intent = Intent(activity, EditMarriageActivity::class.java)
                .putExtra(EditMarriageActivity.EXTRA_WRITE_DATA, true)
                .putExtra(EditMarriageActivity.EXTRA_EXISTING_PERSON, person)
        startActivityForResult(intent, REQUEST_ADD_MARRIAGE)
    }

    private fun addChild() {
        val intent = Intent(activity, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_CHILD)
    }

    private fun switchTreeTo(person: Person) {
        throw IllegalArgumentException("not implemented") // TODO
    }

}
