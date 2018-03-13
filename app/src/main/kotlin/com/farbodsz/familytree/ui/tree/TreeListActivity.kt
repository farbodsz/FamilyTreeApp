package com.farbodsz.familytree.ui.tree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeListItem
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.person.CreatePersonActivity
import com.farbodsz.familytree.ui.person.ViewPersonActivity
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation

/**
 * Activity for displaying the tree as an indented list (a "vertical tree").
 *
 * @see TreeActivity
 */
class TreeListActivity : NavigationDrawerActivity() {

    companion object {

        /**
         * Request code for starting [CreatePersonActivity] for result, to add a new person to the
         * database.
         */
        private const val REQUEST_PERSON_CREATE = 8

        /**
         * Request code for starting [ViewPersonActivity] for result, to view the details of a
         * [Person].
         */
        private const val REQUEST_VIEW_PERSON = 9
    }

    private lateinit var treeAdapter: FamilyTreeAdapter

    private lateinit var items: ArrayList<TreeListItem<Person>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setupFab()
        populateList()
    }

    private fun setupFab() {
        val addPersonButton = findViewById<FloatingActionButton>(R.id.fab)
        addPersonButton.setOnClickListener { addPerson() }
    }

    private fun addPerson() {
        val intent = Intent(this, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_PERSON_CREATE)
    }

    private fun populateList() {
        val root = TreeHandler.getDisplayedTree(this, null)
        items = root?.asTreeList() as ArrayList<TreeListItem<Person>>

        treeAdapter = FamilyTreeAdapter(this, items)
        treeAdapter.onItemClick { _, item -> viewPerson(item.data) }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = treeAdapter
        }
    }

    private fun viewPerson(person: Person) {
        val intent = Intent(this, ViewPersonActivity::class.java)
                .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
        startActivityForResult(intent, REQUEST_VIEW_PERSON)
    }

    /**
     * Refreshes the list by fetching the data (list) from the database and displaying it in the UI
     */
    private fun refreshList() {
        items.clear()
        val newRoot = TreeHandler.getDisplayedTree(this, null)
        items.addAll(newRoot?.asTreeList() as ArrayList<TreeListItem<Person>>)
        treeAdapter.notifyDataSetChanged()
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE_LIST, findViewById(R.id.toolbar))

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERSON_CREATE || requestCode == REQUEST_VIEW_PERSON) {
            if (resultCode == Activity.RESULT_OK) {
                // Refresh list
                refreshList()
            }
        }
    }

}
