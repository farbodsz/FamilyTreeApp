package co.familytreeapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import co.familytreeapp.R
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
import co.familytreeapp.ui.widget.DateViewHelper
import co.familytreeapp.util.toTitleCase

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

    /**
     * The [Person] received via intent extra from the previous activity. If a new person is being
     * created (hence no intent extra), then this will be null.
     *
     * This [Person] will not be affected by changes made in this activity.
     */
    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener { sendCancelledResult() }

        person = intent.extras?.getParcelable(EXTRA_PERSON)

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
    }

    private fun setupLayout() {
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

            dateOfBirthHelper.date = it.dateOfBirth
            dateOfDeathHelper.date = it.dateOfDeath
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
     * Validates the user input and writes it to the database.
     */
    private fun saveData() {
        val validator = Validator(coordinatorLayout)

        val forename = forenameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        if (!validator.checkNames(forename, surname)) return

        val gender = if (maleRadioBtn.isChecked) Gender.MALE else Gender.FEMALE

        val dateOfBirth = dateOfBirthHelper.date
        val dateOfDeath = if (isAliveCheckBox.isChecked) null else dateOfDeathHelper.date
        if (!validator.checkDates(dateOfBirth, dateOfDeath)) return

        val newPerson = Person(
                chooseId(),
                forename.trim().toTitleCase('-'),
                surname.trim().toTitleCase('-'),
                gender,
                dateOfBirth!!,
                placeOfBirthInput.text.toString().trim().toTitleCase(),
                dateOfDeath,
                placeOfDeathInput.text.toString().trim().toTitleCase()
        )

        if (person == null) {
            personManager.add(newPerson)
        } else {
            personManager.update(person!!.id, newPerson)
        }

        sendSuccessfulResult(newPerson)
    }

    /**
     * Returns the id to be used for the [Person] being written to the database.
     * If the [Person] is being modified, the id will remain the same, otherwise it will be a new id.
     */
    private fun chooseId() = person?.id ?: personManager.nextAvailableId()

    /**
     * Sends an "ok" result back to where this activity was invoked from.
     *
     * @param result        the new/updated/deleted [Person]. If deleted this must be null.
     *
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
