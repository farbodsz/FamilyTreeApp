package co.familytreeapp.ui

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import co.familytreeapp.R
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
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

    private lateinit var dateOfBirthSelector: EditText // TODO
    private lateinit var placeOfBirthInput: EditText
    private lateinit var isAliveCheckBox: CheckBox
    private lateinit var dateOfDeathSelector: EditText // TODO
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

        dateOfBirthSelector = findViewById(R.id.editText_dateOfBirth)
        placeOfBirthInput = findViewById(R.id.editText_placeOfBirth)

        isAliveCheckBox = findViewById(R.id.checkbox_alive)
        isAliveCheckBox.setOnCheckedChangeListener { _, isChecked -> setPersonAlive(isChecked) }

        dateOfDeathSelector = findViewById(R.id.editText_dateOfDeath)
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

        val dateOfBirth = LocalDate.now() // TODO

        val dateOfDeath = if (isAliveCheckBox.isChecked) LocalDate.now() else null // TODO

        val person = Person(
                chooseId(),
                forename,
                surname,
                gender,
                dateOfBirth,
                placeOfBirthInput.text.toString().trim(), // TODO "title()" function?
                null,
                placeOfDeathInput.text.toString().trim(), // TODO sim.
                emptyList() // TODO
        )

        // TODO write to db
    }

    /**
     * Returns the id to be used for the [Person] being written to the database.
     * If the [Person] is being modified, the id will remain the same, otherwise it will be a new id.
     */
    private fun chooseId() = person?.id ?: 1 // TODO 1 should be

    /**
     * Validates the forename and surname, showing a message in the UI if invalid.
     *
     * @return true if valid
     */
    private fun validateNames(forename: String, surname: String): Boolean {
        if (forename.isBlank() || surname.isBlank()) {
            Snackbar.make(coordinatorLayout, R.string.validate_name_empty, Snackbar.LENGTH_SHORT)
            return false
        }
        return true
    }

}
