package co.familytreeapp.ui

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import co.familytreeapp.R
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
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
        private const val EXTRA_PERSON = "extra_person"
    }

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

    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_person)

        setSupportActionBar(findViewById(R.id.toolbar))

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
        val forename = forenameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        if (!validateNames(forename, surname)) return

        val gender = if (maleRadioBtn.isChecked) Gender.MALE else Gender.FEMALE

        val dateOfBirth = dateOfBirthHelper.date
        val dateOfDeath = if (isAliveCheckBox.isChecked) null else dateOfDeathHelper.date
        if (!validateDates(dateOfBirth, dateOfDeath)) return

        val person = Person(
                chooseId(),
                forename,
                surname,
                gender,
                dateOfBirth!!,
                placeOfBirthInput.text.toString().trim().toTitleCase('-'),
                dateOfDeath,
                placeOfDeathInput.text.toString().trim().toTitleCase('-')
        )

        // TODO write to db
    }

    /**
     * Returns the id to be used for the [Person] being written to the database.
     * If the [Person] is being modified, the id will remain the same, otherwise it will be a new id.
     */
    private fun chooseId() = person?.id ?: 1 // TODO 1 should be

    /**
     * Checks that the forename and surname are not blank, showing a message in the UI if so.
     *
     * @return true if valid
     */
    private fun validateNames(forename: String, surname: String): Boolean {
        if (forename.isBlank() || surname.isBlank()) {
            Snackbar.make(
                    coordinatorLayout,
                    R.string.validate_name_empty,
                    Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    /**
     * Checks that the date of birth is not null, not in the future, and before the date of death
     * if applicable.
     *
     * @return true if valid
     */
    private fun validateDates(dateOfBirth: LocalDate?, dateOfDeath: LocalDate?): Boolean {
        fun showMessage(@StringRes stringRes: Int) =
                Snackbar.make(coordinatorLayout, stringRes, Snackbar.LENGTH_SHORT).show()

        if (dateOfBirth == null) {
            showMessage(R.string.validate_dateOfBirth_empty)
            return false
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            showMessage(R.string.validate_dateOfBirth_future)
            return false
        }

        if (dateOfDeath != null && dateOfDeath.isBefore(dateOfBirth)) {
            showMessage(R.string.validate_dateOfDeath_beforeBirth)
            return false
        }

        return true
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

}
