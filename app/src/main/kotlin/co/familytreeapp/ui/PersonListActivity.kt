package co.familytreeapp.ui

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setSupportActionBar(findViewById(R.id.toolbar))

        setupFab()
        populateList()
    }

    private fun setupFab() {
        val addPersonButton = findViewById<FloatingActionButton>(R.id.fab)
        addPersonButton.setOnClickListener { editPerson(null) }
    }

    private fun populateList() {
        val people = PersonManager(this).getAll()
        // people.sort() TODO

        val adapter = PersonAdapter(this, people)
        adapter.onItemClick { _, person -> editPerson(person) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            setAdapter(adapter)
        }
    }

    /**
     * Starts [EditPersonActivity] to create/edit a [Person].
     *
     * @param person    the person to be passed to [EditPersonActivity]. This would be null if a new
     *                  person is being created.
     */
    private fun editPerson(person: Person?) {
        val intent = Intent(this, EditPersonActivity::class.java)
        person?.let { intent.putExtra(EditPersonActivity.EXTRA_PERSON, it) }
        startActivity(intent)
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE_LIST, findViewById(R.id.toolbar))

}
