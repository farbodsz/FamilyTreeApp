package com.farbodsz.familytree.ui.person

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.ChildrenManager
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Gender
import com.farbodsz.familytree.model.Marriage
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.DateSelectorHelper
import com.farbodsz.familytree.ui.Validator
import com.farbodsz.familytree.ui.marriage.MarriageAdapter
import com.farbodsz.familytree.util.OnClick
import com.farbodsz.familytree.util.setDateRangePickerConstraints
import com.farbodsz.familytree.util.toTitleCase
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Defines functions that all "person creator" classes must implement.
 *
 * These are classes that can setup a page of the UI for adding/editing details about a person,
 * handle user inputs, and write data to the database after any necessary validation.
 */
interface PersonCreatorSection {

    /**
     * Sets up the layout for this page of the activity.
     *
     * @param layoutInflater    the activity's layout inflater for instantiating XML resources
     * @param viewPager         the [ViewPager] used to organise pages in the activity (i.e. the
     *                          parent of the page layout)
     * @return the inflated and set up page layout [View]
     */
    fun setupPageLayout(layoutInflater: LayoutInflater, viewPager: ViewPager): View

    /**
     * Writes data to the database, validating if necessary.
     *
     * @return true if the data was written successfully; false if the data was invalid
     */
    fun writeData(): Boolean

}

typealias OnCreateNew = (dialogInterface: DialogInterface, which: Int) -> Unit

class PersonDetailsCreator(
        private val context: Context,
        private val personId: Int,
        private val validator: Validator,
        private val onPostWriteData: (newPerson: Person) -> Unit,
        private val onSelectPersonImage: OnClick
) : PersonCreatorSection {

    private lateinit var circleImageView: CircleImageView

    private lateinit var forenameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var maleRadioBtn: RadioButton

    private lateinit var dateOfBirthSelector: DateSelectorHelper
    private lateinit var placeOfBirthInput: TextInputEditText
    private lateinit var dateOfDeathSelector: DateSelectorHelper
    private lateinit var placeOfDeathInput: TextInputEditText

    private lateinit var isAliveCheckBox: CheckBox

    override fun setupPageLayout(layoutInflater: LayoutInflater, viewPager: ViewPager): View {
        val page = layoutInflater.inflate(R.layout.fragment_create_person_page, viewPager, false)
        val container = page.findViewById<ViewGroup>(R.id.container)

        container.addView(createPersonDetailsCard(layoutInflater, container))
        container.addView(createDatesCard(layoutInflater, container))

        return page
    }

    private fun createPersonDetailsCard(layoutInflater: LayoutInflater, container: ViewGroup): View {
        val card = layoutInflater.inflate(R.layout.card_edit_person_details, container, false)

        circleImageView = card.findViewById(R.id.circleImageView)
        circleImageView.setOnClickListener { view -> onSelectPersonImage.invoke(view) }

        forenameInput = card.findViewById(R.id.editText_forename)
        surnameInput = card.findViewById(R.id.editText_surname)

        setupNameInputError(card.findViewById(R.id.textInputLayout_forename), forenameInput)
        setupNameInputError(card.findViewById(R.id.textInputLayout_surname), surnameInput)

        maleRadioBtn = card.findViewById(R.id.rBtn_male)
        maleRadioBtn.setOnCheckedChangeListener { _, isChecked -> setGender(isChecked) }
        maleRadioBtn.isChecked = true
        // Female radio button will respond accordingly since they are in the same RadioGroup

        return card
    }

    private fun setupNameInputError(textInputLayout: TextInputLayout,
                                    editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isBlank()) {
                    textInputLayout.error = context.getString(R.string.error_name_empty)
                } else {
                    textInputLayout.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editText.text.isBlank()) {
                    textInputLayout.error = context.getString(R.string.error_name_empty)
                } else {
                    textInputLayout.isErrorEnabled = false
                }
            }
        }
    }

    /**
     * Modifies the UI components after a gender has been selected.
     */
    private fun setGender(isMale: Boolean) {
        val gender = if (isMale) Gender.MALE else Gender.FEMALE
        circleImageView.borderColor = ContextCompat.getColor(context, gender.getColorRes())
    }

    /**
     * Sets the image to use for the person.
     *
     * This should be called when an image has been selected from an [Intent] via the activity this
     * is being invoked from.
     */
    fun setPersonImage() {
        Log.w("PersonCreatorHelper", "RECEIVED")
    }

    private fun createDatesCard(layoutInflater: LayoutInflater, container: ViewGroup): View {
        val card = layoutInflater.inflate(R.layout.card_edit_birth_death, container, false)

        dateOfBirthSelector =
                DateSelectorHelper(context, card.findViewById(R.id.editText_dateOfBirth))
        placeOfBirthInput = card.findViewById(R.id.editText_placeOfBirth)

        dateOfDeathSelector =
                DateSelectorHelper(context, card.findViewById(R.id.editText_dateOfDeath))
        placeOfDeathInput = card.findViewById(R.id.editText_placeOfDeath)

        isAliveCheckBox = card.findViewById<CheckBox>(R.id.checkbox_alive).apply {
            setOnCheckedChangeListener { _, isChecked -> setPersonAlive(card, isChecked) }
        }

        setPersonAlive(card, true) // isAlive by default
        setDateRangePickerConstraints(dateOfBirthSelector, dateOfDeathSelector)

        return card
    }

    /**
     * Toggles the checkbox and other UI components for whether or not a person [isAlive].
     * This should be used instead of toggling the checkbox manually.
     */
    private fun setPersonAlive(card: View, isAlive: Boolean) {
        isAliveCheckBox.isChecked = isAlive
        card.findViewById<LinearLayout>(R.id.group_deathInfo).visibility =
                if (isAlive) View.GONE else View.VISIBLE
    }

    override fun writeData(): Boolean {
        // Don't continue with db write if inputs invalid
        val newPerson = validatePerson(validator) ?: return false
        PersonManager(context).add(newPerson)
        onPostWriteData.invoke(newPerson)
        return true
    }

    /**
     * Validates the user inputs and constructs a [Person] object from it.
     *
     * @return  the constructed [Person] object if user inputs are valid. If one or more user inputs
     *          are invalid, then this will return null.
     */
    private fun validatePerson(validator: Validator): Person? {
        val forename = forenameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        if (!validator.checkNames(forename, surname)) return null

        val gender = if (maleRadioBtn.isChecked) Gender.MALE else Gender.FEMALE

        // Dates should be ok from dialog constraint, but best to double-check before db write
        val dateOfBirth = dateOfBirthSelector.date
        val dateOfDeath = if (isAliveCheckBox.isChecked) null else dateOfDeathSelector.date
        if (!validator.checkDates(dateOfBirth, dateOfDeath)) return null

        return Person(
                personId,
                forename.trim().toTitleCase('-'),
                surname.trim().toTitleCase('-'),
                gender,
                dateOfBirth!!,
                placeOfBirthInput.text.toString().trim().toTitleCase(),
                dateOfDeath,
                placeOfDeathInput.text.toString().trim().toTitleCase()
        )
    }

}

class PersonMarriageCreator(
        private val context: Context,
        private val person: Person,
        private val onCreateNewMarriage: OnCreateNew
) : PersonCreatorSection {

    private lateinit var marriageRecyclerView: RecyclerView

    private lateinit var marriages: ArrayList<Marriage>

    override fun setupPageLayout(layoutInflater: LayoutInflater, viewPager: ViewPager): View {
        val page = layoutInflater.inflate(R.layout.fragment_create_person_page, viewPager, false)
        val container = page.findViewById<ViewGroup>(R.id.container)
        val card = layoutInflater.inflate(R.layout.card_edit_marriages, container, false)

        marriageRecyclerView = card.findViewById<RecyclerView>(R.id.recyclerView_marriages).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        val addMarriageButton = card.findViewById<Button>(R.id.button_addMarriage)
        addMarriageButton.setOnClickListener {
            chooseMarriageDialog()
        }

        setupMarriageList()

        return card
    }

    private fun chooseMarriageDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(context)

        val dialogView = TextView(context).apply { setText(R.string.db_marriages_empty) }

        builder.setView(dialogView)
                .setTitle(R.string.dialog_add_marriage_title)
                .setPositiveButton(R.string.action_create_new) { dialogInterface, which ->
                    onCreateNewMarriage.invoke(dialogInterface, which)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->  }

        dialog = builder.show()
    }

    /**
     * Sets up [marriageRecyclerView] to display the marriages of the [Person] being edited.
     */
    private fun setupMarriageList() {
        marriages = MarriagesManager(context).getMarriages(person.id) as ArrayList<Marriage>

        val marriageAdapter = MarriageAdapter(context, person.id, marriages)
        marriageAdapter.onItemClick { _, marriage ->
            // Show dialog with option to delete
            val options = arrayOf(context.getString(R.string.action_delete))

            AlertDialog.Builder(context).setTitle(context.getString(R.string.marriage_with, person))
                    .setItems(options) { _, which -> removeMarriage(marriage) }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->  }
                    .show()
        }

        marriageRecyclerView.adapter = marriageAdapter
    }

    /**
     * Updates the UI to add a [marriage] to the [Person] being edited.
     * This function does not write anything to the database.
     *
     * @see removeMarriage
     */
    fun addMarriage(marriage: Marriage) {
        marriages.add(marriage)
        marriageRecyclerView.adapter.notifyDataSetChanged()
    }

    /**
     * Updates the UI to remove a [marriage] from the [Person] being edited.
     * This function does not delete anything from the database.
     *
     * @see addMarriage
     */
    private fun removeMarriage(marriage: Marriage) {
        marriages.remove(marriage)
        marriageRecyclerView.adapter.notifyDataSetChanged()
    }

    override fun writeData() = true // data will be written in instances of EditMarriageActivity

}

class PersonChildrenCreator(
        private val context: Context,
        private val parent: Person,
        private val onCreateNewChild: OnCreateNew
) : PersonCreatorSection {

    private lateinit var childrenRecyclerView: RecyclerView
    private lateinit var childrenText: TextView

    private lateinit var children: ArrayList<Person>

    override fun setupPageLayout(layoutInflater: LayoutInflater, viewPager: ViewPager): View {
        val page = layoutInflater.inflate(R.layout.fragment_create_person_page, viewPager, false)
        val container = page.findViewById<ViewGroup>(R.id.container)
        val card = layoutInflater.inflate(R.layout.card_edit_children, container, false)

        childrenText = card.findViewById(R.id.text_childrenNum)
        childrenText.text = context.resources.getQuantityText(R.plurals.children_count_subtitle, 0)

        childrenRecyclerView = card.findViewById<RecyclerView>(R.id.recyclerView_children).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        val addChildButton = card.findViewById<Button>(R.id.button_addChild)
        addChildButton.setOnClickListener {
            chooseChildDialog(layoutInflater)
        }

        setupChildrenList()

        return card
    }

    private fun chooseChildDialog(layoutInflater: LayoutInflater) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(context)

        val personAdapter = PersonAdapter(context, getPotentialChildren())
        personAdapter.onItemClick { _, person ->
            addChild(person)
            dialog.dismiss()
        }

        val recyclerView = RecyclerView(context).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }

        val titleView = layoutInflater.inflate(R.layout.dialog_title_subtitle, null).apply {
            findViewById<TextView>(R.id.title).setText(R.string.dialog_add_child_title)
            findViewById<TextView>(R.id.subtitle).setText(R.string.dialog_add_child_subtitle)
        }

        builder.setView(recyclerView)
                .setCustomTitle(titleView)
                .setPositiveButton(R.string.action_create_new) { dialogInterface, which ->
                    onCreateNewChild.invoke(dialogInterface, which)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->  }

        dialog = builder.show()
    }

    /**
     * Returns a list of people that could be the child of this [parent].
     *
     * This is people younger than this [parent], not already considered a child of him/her, and not
     * the [parent] itself.
     */
    private fun getPotentialChildren(): List<Person> {
        val potentialChildren = ArrayList<Person>()

        // Ok to use this DOB as there were constraints/validation on the dialog picker
        val personManager = PersonManager(context)

        for (child in personManager.getAll()) {
            if (child.id != parent.id
                    && child !in children
                    && child.dateOfBirth.isAfter(parent.dateOfBirth)) {
                potentialChildren.add(child)
            }
        }
        return potentialChildren
    }

    /**
     * Sets up [childrenRecyclerView] to display the children of the [Person] being edited.
     */
    private fun setupChildrenList() {
        val childrenManager = ChildrenManager(context)
        children = childrenManager.getChildren(parent.id) as ArrayList<Person>

        childrenText.text = context.resources.getQuantityString(
                R.plurals.children_count_subtitle,
                children.count(),
                children.count()
        )

        val personAdapter = PersonAdapter(context, children)
        personAdapter.onItemClick { _, person ->
            // Show dialog with option to delete
            val options = arrayOf(context.getString(R.string.action_delete))

            AlertDialog.Builder(context).setTitle(person.fullName)
                    .setItems(options) { _, which -> removeChild(person) }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->  }
                    .show()
        }

        childrenRecyclerView.adapter = personAdapter
    }

    /**
     * Updates the UI to add a [child] to the [Person] being edited.
     * This function does not add anything to the database.
     *
     * @see removeChild
     */
    fun addChild(child: Person) {
        children.add(child)
        childrenRecyclerView.adapter.notifyDataSetChanged()
        childrenText.text = context.resources.getQuantityString(
                R.plurals.children_count_subtitle,
                children.count(),
                children.count()
        )
    }

    /**
     * Updates the UI to remove a [child] from the [Person] being edited.
     * This function does not delete anything from the database.
     *
     * @see addChild
     */
    private fun removeChild(child: Person) {
        children.remove(child)
        childrenRecyclerView.adapter.notifyDataSetChanged()
        childrenText.text = context.resources.getQuantityString(
                R.plurals.children_count_subtitle,
                children.count(),
                children.count()
        )
    }

    override fun writeData(): Boolean {
        // While the Person objects from the child-parent relationship have been added, the
        // relationships themselves have not. Data validation doesn't need to be done here.
        ChildrenManager(context).addChildren(parent.id, children)
        return true
    }
}
