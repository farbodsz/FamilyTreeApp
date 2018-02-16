package co.familytreeapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import co.familytreeapp.R
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Person
import co.familytreeapp.ui.adapter.PersonAdapter
import co.familytreeapp.util.standardNavigationParams
import co.familytreeapp.util.withNavigation

/**
 * Activity for displaying the tree as a vertical list with indents.
 */
class PersonListActivity : NavigationDrawerActivity() {

    companion object {

        /**
         * Request code for starting [ViewPersonActivity] for result.
         */
        private const val REQUEST_PERSON_VIEW = 1

        /**
         * Request code for starting [EditPersonActivity] for result.
         */
        private const val REQUEST_PERSON_EDIT = 2
    }

    private val personManager = PersonManager(this)
    private lateinit var people: ArrayList<Person>

    private lateinit var personAdapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setSupportActionBar(findViewById(R.id.toolbar))

        setupFab()
        populateList()
    }

    private fun setupFab() {
        val addPersonButton = findViewById<FloatingActionButton>(R.id.fab)
        addPersonButton.setOnClickListener { addPerson() }
    }

    private fun populateList() {
        people = personManager.getAll() as ArrayList<Person>
        people.sort()

        personAdapter = PersonAdapter(this, people)
        personAdapter.onItemClick { _, person -> viewPerson(person) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = personAdapter
        }
    }

    /**
     * Refreshes the list by fetching the data (list) from the database and displaying it in the UI
     */
    private fun refreshList() {
        people.clear()
        people.addAll(personManager.getAll())
        people.sort()
        personAdapter.notifyDataSetChanged()
    }

    /**
     * Starts [ViewPersonActivity] to view a [Person], for result.
     * @param person    the person to be passed to [ViewPersonActivity]
     */
    private fun viewPerson(person: Person) {
        val intent = Intent(this, ViewPersonActivity::class.java)
                .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
        startActivityForResult(intent, REQUEST_PERSON_VIEW)
    }

    /**
     * Starts [EditPersonActivity] to create a [Person], for result.
     */
    private fun addPerson() {
        val intent = Intent(this, EditPersonActivity::class.java)
        startActivityForResult(intent, REQUEST_PERSON_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in arrayOf(REQUEST_PERSON_VIEW, REQUEST_PERSON_EDIT)) {
            // A person could be modified by starting EditPersonActivity from ViewPersonActivity

            if (resultCode == Activity.RESULT_OK) {
                // Refresh the list
                refreshList()
            }
        }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_PERSON_LIST, findViewById(R.id.toolbar))

}
