package co.familytreeapp.ui.person

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import co.familytreeapp.R
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Marriage
import co.familytreeapp.model.Person
import co.familytreeapp.ui.Validator
import co.familytreeapp.ui.adapter.DynamicPagerAdapter
import co.familytreeapp.ui.marriage.EditMarriageActivity

/**
 * This activity provides the UI for adding a new person from the database, with a guided format.
 *
 * **To edit an existing person, [EditPersonActivity] should be used, not this.**
 *
 * When the user adds all the information and confirms, the data for the new person will be written
 * to the database, and the newly created [Person] will be sent back to the activity from which this
 * was started as a result.
 *
 * @see EditPersonActivity
 */
class CreatePersonActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "CreatePersonActivity"

        /**
         * Intent extra key for *returning* a [Person] from the calling activity.
         *
         * This activity is only for creating a new [Person], so anything with this key passed to
         * this activity will be ignored.
         */
        const val EXTRA_PERSON = "extra_person"

        /**
         * Request code for starting [CreatePersonActivity] for result, to create a new [Person]
         * which would be the child of the [person] (parent).
         */
        private const val REQUEST_CREATE_CHILD = 4

        /**
         * Request code for starting [EditMarriageActivity] for result, to create a new [Marriage]
         */
        private const val REQUEST_CREATE_MARRIAGE = 5

        /**
         * The number of pages to be displayed in this activity.
         */
        private const val NUM_PAGES = 3
    }


    private val personManager = PersonManager(this)

    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var viewPager: ViewPager
    private val pagerAdapter = DynamicPagerAdapter()

    private lateinit var nextButton: Button

    /**
     * The creator class for adding marriages to the person.
     *
     * It will be initialised (and so should only be accessed) only after creating the [Person]
     * object and writing it to the database.
     *
     * @see childrenCreator
     * @see person
     */
    private lateinit var marriageCreator: PersonMarriageCreator

    /**
     * The creator class for adding children to the person.
     *
     * It will be initialised (and so should only be accessed) only after creating the [Person]
     * object and writing it to the database.
     *
     * @see marriageCreator
     * @see person
     */
    private lateinit var childrenCreator: PersonChildrenCreator

    /**
     * The ID assigned to this new [Person] being created.
     *
     * As a lazily-initialised variable, its value will remain constant, so it will have the same
     * value before and after the [Person] has been written to the database.
     */
    private val personId by lazy { personManager.nextAvailableId() }

    /**
     * The newly created [Person] (already written to the database).
     *
     * It will be assigned after a [PersonDetailsCreator] writes to the database; until then it will
     * remain at its default value of null.
     */
    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_person)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            if (personHasCreated()) sendSuccessfulResult(person!!) else sendCancelledResult()
        }

        setupLayout()
    }

    private fun personHasCreated() = person != null

    private fun setupLayout() {
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager).apply { adapter = pagerAdapter }
        nextButton = findViewById(R.id.button_next)

        setupPersonCreatorPage(0)
    }

    /**
     * Sets up a page of the layout, determined by the [pageIndex].
     *
     * @param pageIndex an integer identifying the page being set up. This is not the same as the
     *                  position of the page in the [viewPager].
     */
    private fun setupPersonCreatorPage(pageIndex: Int) {
        val creator = getCreatorClass(pageIndex)

        // Change "Next" button's test to "Done" if last page
        if (pageIndex == NUM_PAGES - 1) nextButton.setText(R.string.action_done)

        // Add the new page and go to it
        val page = creator.setupPageLayout(layoutInflater, viewPager)
        val pagePos = pagerAdapter.addView(page)
        viewPager.currentItem = pagePos

        // Remove the previous page so the user cannot swipe back
        if (pagePos > 0) pagerAdapter.removeView(viewPager, pagePos - 1)

        // Change action for when the next button is pressed
        nextButton.setOnClickListener {
            if (!creator.writeData()) return@setOnClickListener
            // Only continue if data successfully written to database (i.e. not invalid data)

            if (pageIndex < NUM_PAGES - 1) {
                setupPersonCreatorPage(pageIndex + 1) // setup the next page
            } else {
                completePersonCreation()
            }
        }
    }

    /**
     * Returns the creator class responsible for displaying the page with [index].
     */
    private fun getCreatorClass(index: Int) = when (index) {
        0 -> PersonDetailsCreator(this, personId, Validator(coordinatorLayout)) { newPerson ->
            person = newPerson
        }
        1 -> {
            marriageCreator = PersonMarriageCreator(this, person!!) { _, _ ->
                val intent = Intent(this, EditMarriageActivity::class.java)
                        .putExtra(EditMarriageActivity.EXTRA_EXISTING_PERSON, person!!)
                startActivityForResult(intent, REQUEST_CREATE_MARRIAGE)
            }
            marriageCreator
        }
        2 -> {
            childrenCreator = PersonChildrenCreator(this, person!!) { _, _ ->
                val intent = Intent(this, CreatePersonActivity::class.java)
                startActivityForResult(intent, REQUEST_CREATE_CHILD)
            }
            childrenCreator
        }
        else -> throw IllegalArgumentException("invalid index: $index")
    }

    private fun completePersonCreation() {
        // Data has already been written to the database
        // Just send back a successful result
        sendSuccessfulResult(person!!)
    }

    /**
     * Sends an "ok" result back to where this activity was invoked from.
     *
     * @param result    the newly created [Person]
     * @see android.app.Activity.RESULT_OK
     */
    private fun sendSuccessfulResult(result: Person) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CREATE_CHILD -> if (resultCode == Activity.RESULT_OK) {
                // User has successfully created a new child from the dialog
                val child = data!!.getParcelableExtra<Person>(CreatePersonActivity.EXTRA_PERSON)
                childrenCreator.addChild(child)
            }
            REQUEST_CREATE_MARRIAGE -> if (resultCode == Activity.RESULT_OK) {
                // User has successfully created a new marriage from the dialog
                val marriage = data!!.getParcelableExtra<Marriage>(EditMarriageActivity.EXTRA_MARRIAGE)
                marriageCreator.addMarriage(marriage)
            }
        }
    }

    override fun onBackPressed() = sendCancelledResult()

}
