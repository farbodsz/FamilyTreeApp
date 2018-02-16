package co.familytreeapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.database.manager.ChildrenManager
import co.familytreeapp.model.Person
import co.familytreeapp.ui.adapter.PersonAdapter
import co.familytreeapp.util.DATE_FORMATTER_BIRTH

/**
 * Activity for displaying the details of a person.
 *
 * This activity is not responsible for editing a person: for such cases, [EditPersonActivity]
 * should be started for result (it will return the new/updated object to the calling activity).
 *
 * While this activity does not deal with editing a person, it does provide the option to edit the
 * person being displayed by starting [EditPersonActivity] for result. Consequently, **this activity
 * should be started for result, because it could return an updated person object to the calling
 * activity.**
 */
class ViewPersonActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "ViewPersonActivity"

        /**
         * Request code for starting [EditPersonActivity] for result.
         */
        private const val REQUEST_PERSON_EDIT = 3

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        const val EXTRA_PERSON = "extra_person"
    }

    /**
     * The [Person] received via intent extra from the previous activity.
     *
     * It cannot be null. To create a new [Person], [EditPersonActivity] should be used, since this
     * this activity is only for displaying existing [Person]s.
     */
    private lateinit var person: Person

    private var hasBeenModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        person = intent.extras?.getParcelable(EXTRA_PERSON)
                ?: // received Person could be null, throw exception if so
                throw IllegalArgumentException("ViewPersonActivity cannot display a null person")

        setupLayout()
    }

    private fun setupLayout() {
        val genderText = findViewById<TextView>(R.id.text_gender)
        if (person.gender.isMale()) {
            genderText.setText(R.string.male)
            genderText.setTextColor(ContextCompat.getColor(this, R.color.image_border_male))
        } else {
            genderText.setText(R.string.female)
            genderText.setTextColor(ContextCompat.getColor(this, R.color.image_border_female))
        }

        findViewById<TextView>(R.id.text_birth).text =
                person.dateOfBirth.format(DATE_FORMATTER_BIRTH)

        if (person.isAlive()) {
            findViewById<LinearLayout>(R.id.group_deathInfo).visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.text_death).text =
                    person.dateOfDeath!!.format(DATE_FORMATTER_BIRTH)
        }

        setupChildrenList()
    }

    private fun setupChildrenList() {
        val children = ChildrenManager(this).getChildren(person.id)
        val personAdapter = PersonAdapter(this, children)
        findViewById<RecyclerView>(R.id.recyclerView_children).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = personAdapter
        }
    }

    /**
     * Sends the correct result back to where this activity was invoked from, and finishes the
     * activity.
     *
     * An "ok" result will be used if the [person] has been modified, otherwise a "cancelled" result.
     *
     * @see android.app.Activity.RESULT_OK
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendResult() {
        if (hasBeenModified) {
            Log.d(LOG_TAG, "Sending successful result: $person")
            val returnIntent = Intent().putExtra(EXTRA_PERSON, person)
            setResult(Activity.RESULT_OK, returnIntent)
        } else {
            Log.d(LOG_TAG, "Sending cancelled result")
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_view_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_edit -> {
                val intent = Intent(this, EditPersonActivity::class.java)
                        .putExtra(EditPersonActivity.EXTRA_PERSON, person)
                startActivityForResult(intent, REQUEST_PERSON_EDIT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = sendResult()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERSON_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                hasBeenModified = true
                person = data!!.getParcelableExtra(EditPersonActivity.EXTRA_PERSON)
                setupLayout() // update UI
            }
        }
    }

}
