package co.familytreeapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import co.familytreeapp.R
import co.familytreeapp.database.manager.ChildrenManager
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
import co.familytreeapp.ui.adapter.PersonAdapter
import co.familytreeapp.ui.widget.DateViewHelper
import co.familytreeapp.util.toTitleCase
import org.threeten.bp.LocalDate

/**
 * This activity provides the UI for adding or editing a new person from the database.
 *
 * When the user adds all the information and confirms, the data for the new person will be written
 * to the database, and the newly created [Person] will be sent back to the activity from which this
 * was started as a result.
 */
class EditPersonActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "EditPersonActivity"

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        const val EXTRA_PERSON = "extra_person"
    }

    private val personManager = PersonManager(this)

    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var forenameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var maleRadioBtn: RadioButton
    private lateinit var femaleRadioBtn: RadioButton

    private lateinit var dateOfBirthHelper: DateViewHelper
    private lateinit var placeOfBirthInput: EditText
    private lateinit var isAliveCheckBox: CheckBox
    private lateinit var dateOfDeathHelper: DateViewHelper
    private lateinit var placeOfDeathInput: EditText

    private lateinit var childrenText: TextView
    private lateinit var childrenRecyclerView: RecyclerView

    /**
     * The [Person] received via intent extra from the previous activity. If a new person is being
     * created (hence no intent extra), then this will be null.
     *
     * This [Person] will not be affected by changes made in this activity.
     */
    private var person: Person? = null

    /**
     * The list of this [person]'s children that are displayed in the UI.
     * When the "Done" action is selected, these will be added to the database.
     */
    private lateinit var children: ArrayList<Person>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener { sendCancelledResult() }

        person = intent.extras?.getParcelable(EXTRA_PERSON)

        if (person == null) {
            Log.v(LOG_TAG, "Editing a new person")
        } else {
            Log.v(LOG_TAG, "Editing an existing person: $person")
        }

        assignUiComponents()

        setupLayout()
    }

    /**
     * Assigns the variables for our UI components in the layout.
     */
    private fun assignUiComponents() {
        coordinatorLayout = findViewById(R.id.coordinatorLayout)

        forenameInput = findViewById(R.id.editText_forename)
        surnameInput = findViewById(R.id.editText_surname)

        maleRadioBtn = findViewById(R.id.rBtn_male)
        femaleRadioBtn = findViewById(R.id.rBtn_female)

        dateOfBirthHelper = DateViewHelper(this, findViewById(R.id.editText_dateOfBirth))
        placeOfBirthInput = findViewById(R.id.editText_placeOfBirth)

        isAliveCheckBox = findViewById(R.id.checkbox_alive)
        isAliveCheckBox.setOnCheckedChangeListener { _, isChecked -> setPersonAlive(isChecked) }

        dateOfDeathHelper = DateViewHelper(this, findViewById(R.id.editText_dateOfDeath))
        placeOfDeathInput = findViewById(R.id.editText_placeOfDeath)

        childrenText = findViewById(R.id.text_childrenNum)
        childrenText.text = resources.getQuantityText(R.plurals.children_count_subtitle, 0)

        childrenRecyclerView = findViewById(R.id.recyclerView)
        childrenRecyclerView.layoutManager = LinearLayoutManager(this@EditPersonActivity)

        val addChildButton = findViewById<Button>(R.id.button_addChild)
        addChildButton.setOnClickListener {
            chooseChildDialog()
        }
    }

    private fun setupLayout() {
        setDatePickerConstraints()

        setupChildrenList()

        if (person == null) {
            Log.i(LOG_TAG, "Person is null - setting up the default layout")
            setupDefaultLayout()
            return
        }

        person?.let {
            forenameInput.setText(it.forename)
            surnameInput.setText(it.surname)

            maleRadioBtn.isChecked = it.gender.isMale()
            femaleRadioBtn.isChecked = it.gender.isFemale()

            setPersonAlive(it.isAlive())

            with(dateOfBirthHelper) {
                date = it.dateOfBirth
                if (it.dateOfDeath != null) maxDate = it.dateOfDeath
            }

            with(dateOfDeathHelper) {
                date = it.dateOfDeath
                minDate = it.dateOfBirth
            }
        }
    }

    /**
     * Sets the default date picker minimum/maximum dates.
     * This is based on the current date, and selections made in other date pickers.
     */
    private fun setDatePickerConstraints() {
        with(dateOfBirthHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                dateOfDeathHelper.minDate = newDate
            }
        }

        with(dateOfDeathHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                dateOfBirthHelper.maxDate = newDate
            }
        }
    }

    private fun setupDefaultLayout() {
        maleRadioBtn.isChecked = true
        setPersonAlive(true)
    }

    /**
     * Toggles the checkbox and other UI components for whether or not a person [isAlive].
     * This should be used instead of toggling the checkbox manually.
     */
    private fun setPersonAlive(isAlive: Boolean) {
        isAliveCheckBox.isChecked = isAlive
        findViewById<LinearLayout>(R.id.group_deathInfo).visibility =
                if (isAlive) View.GONE else View.VISIBLE
    }

    /**
     * Sets up [childrenRecyclerView] to display the children of the [Person] being edited.
     *
     * This should be invoked regardless of whether a new person is being added or an existing
     * person is being edited.
     */
    private fun setupChildrenList() {
        val childrenManager = ChildrenManager(this)
        children = childrenManager.getChildren(editedPersonId()) as ArrayList<Person>

        childrenText.text = resources.getQuantityString(
                R.plurals.children_count_subtitle,
                children.count(),
                children.count()
        )
        childrenRecyclerView.adapter = PersonAdapter(this, children)
    }

    private fun chooseChildDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)

        val people = personManager.getAll() as ArrayList<Person>
        people.sort()

        val personAdapter = PersonAdapter(this, people)
        personAdapter.onItemClick { _, person ->
            addChildToUi(person)
            dialog.dismiss()
        }

        val recyclerView = RecyclerView(this)
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@EditPersonActivity)
            adapter = personAdapter
        }

        builder.setView(recyclerView)
                .setNegativeButton(android.R.string.cancel) { _, _ ->  }

        dialog = builder.create()
        dialog.show()
    }

    /**
     * Updates the UI to add a [child] to the [Person] being edited.
     * Nothing is written to the database at this stage.
     */
    private fun addChildToUi(child: Person) {
        children.add(child)
        childrenRecyclerView.adapter.notifyDataSetChanged()
        childrenText.text = resources.getQuantityString(
                R.plurals.children_count_subtitle,
                children.count(),
                children.count()
        )
    }

    /**
     * Validates the user input and writes it to the database.
     */
    private fun saveData() {
        // Don't continue with db write if inputs invalid
        val newPerson = validatePerson() ?: return

        val childrenManager = ChildrenManager(this)
        childrenManager.updateChildren(editedPersonId(), children)

        if (person == null) {
            personManager.add(newPerson)
            sendSuccessfulResult(newPerson)
            return
        }

        if (person!! == newPerson) {
            // Nothing changed, so avoid db write, and nothing will change in result activity
            sendCancelledResult()
        } else {
            personManager.update(person!!.id, newPerson)
            sendSuccessfulResult(newPerson)
        }
    }

    /**
     * Validates the user inputs and constructs a [Person] object from it.
     *
     * @return  the constructed [Person] object if user inputs are valid. If one or more user inputs
     *          are invalid, then this will return null.
     */
    private fun validatePerson(): Person? {
        val validator = Validator(coordinatorLayout)

        val forename = forenameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        if (!validator.checkNames(forename, surname)) return null

        val gender = if (maleRadioBtn.isChecked) Gender.MALE else Gender.FEMALE

        // Dates should be ok from dialog constraint, but best to double-check before db write
        val dateOfBirth = dateOfBirthHelper.date
        val dateOfDeath = if (isAliveCheckBox.isChecked) null else dateOfDeathHelper.date
        if (!validator.checkDates(dateOfBirth, dateOfDeath)) return null

        return Person(
                editedPersonId(),
                forename.trim().toTitleCase('-'),
                surname.trim().toTitleCase('-'),
                gender,
                dateOfBirth!!,
                placeOfBirthInput.text.toString().trim().toTitleCase(),
                dateOfDeath,
                placeOfDeathInput.text.toString().trim().toTitleCase()
        )
    }

    /**
     * Returns the ID to be used for the [Person] being edited in this activity.
     * If the [Person] is being updated, this ID will remain the same as the original [Person]; if
     * it's a new [Person], it will be assigned a new ID.
     */
    private fun editedPersonId() = person?.id ?: personManager.nextAvailableId()

    /**
     * Sends an "ok" result back to where this activity was invoked from.
     *
     * @param result    the new/updated/deleted [Person]. If deleted this must be null.
     * @see android.app.Activity.RESULT_OK
     */
    private fun sendSuccessfulResult(result: Person?) {
        Log.d(LOG_TAG, "Sending successful result: $result")
        val returnIntent = Intent().putExtra(EXTRA_PERSON, result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    /**
     * Sends a "cancelled" result back to where this activity was invoked from.
     *
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendCancelledResult() {
        Log.d(LOG_TAG, "Sending cancelled result")
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_done -> saveData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = sendCancelledResult()

}
