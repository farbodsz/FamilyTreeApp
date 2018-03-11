package com.farbodsz.familytree.ui.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Anniversary
import com.farbodsz.familytree.model.Birthday
import com.farbodsz.familytree.model.Event
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.marriage.ViewMarriageActivity
import com.farbodsz.familytree.ui.person.ViewPersonActivity
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation

/**
 * Activity for displaying a list of birthdays and anniversaries.
 */
class EventsActivity : NavigationDrawerActivity() {

    companion object {

        /**
         * Request code for starting [ViewPersonActivity] for result.
         */
        private const val REQUEST_PERSON_VIEW = 1

        /**
         * Request code for starting [ViewMarriageActivity] for result.
         */
        private const val REQUEST_MARRIAGE_VIEW = 2
    }

    private val eventHandler = EventHandler(this)
    private lateinit var events: ArrayList<Event>

    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setSupportActionBar(findViewById(R.id.toolbar))

        // Won't be using the FAB
        findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE

        populateList()
    }

    private fun populateList() {
        events = eventHandler.getEvents() as ArrayList<Event>
        events.sort()

        eventAdapter = EventAdapter(this, events)
        eventAdapter.onItemClick { _, event -> viewEvent(event) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }
    }

    /**
     * Refreshes the list by fetching the data (list) from the database and displaying it in the UI
     */
    private fun refreshList() {
        events.clear()
        events.addAll(eventHandler.getEvents())
        events.sort()
        eventAdapter.notifyDataSetChanged()
    }

    /**
     * Starts the appropriate activity to view an [event], for result.
     */
    private fun viewEvent(event: Event) = when (event) {
        is Anniversary -> {
            val marriage = MarriagesManager(this).get(event.personIds)
            val intent = Intent(this, ViewMarriageActivity::class.java)
                    .putExtra(ViewMarriageActivity.EXTRA_MARRIAGE, marriage)
            startActivityForResult(intent, REQUEST_MARRIAGE_VIEW)
        }
        is Birthday -> {
            val person = PersonManager(this).get(event.personId)
            val intent = Intent(this, ViewPersonActivity::class.java)
                    .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
            startActivityForResult(intent, REQUEST_PERSON_VIEW)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode in arrayOf(REQUEST_PERSON_VIEW, REQUEST_MARRIAGE_VIEW)) {
            // A person could be modified by starting "edit" activities from "view" activities

            if (resultCode == Activity.RESULT_OK) {
                // Refresh the list
                refreshList()
            }
        }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_EVENTS, findViewById(R.id.toolbar))

}
