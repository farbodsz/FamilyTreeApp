package co.familytreeapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.RadioButton
import co.familytreeapp.R
import co.familytreeapp.model.Person

/**
 * This activity provides the UI for adding a new person to the database.
 *
 * When the user adds all the information and confirms, the data for the new person will be written
 * to the database, and the newly created [Person] will be sent back to the activity from which this
 * was started as a result.
 */
class AddPersonActivity : AppCompatActivity() {

    companion object {

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        private const val EXTRA_PERSON = "extra_person"

    }

    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)

        setSupportActionBar(findViewById(R.id.toolbar))

        //person = intent.extras?.getParcelable<Person>(EXTRA_PERSON)

        setupLayout()
    }

    private fun setupLayout() {
        if (person == null) {
            setupDefaultLayout()
            return
        }
    }

    private fun setupDefaultLayout() {
        findViewById<RadioButton>(R.id.rBtn_male).isSelected = true
        findViewById<CheckBox>(R.id.checkbox_alive).isSelected = true
    }

}
