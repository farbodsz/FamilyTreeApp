package com.farbodsz.familytree.ui.tree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.ChildrenManager
import com.farbodsz.familytree.model.ChildRelationship
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.marriage.EditMarriageActivity
import com.farbodsz.familytree.ui.person.CreatePersonActivity
import com.farbodsz.familytree.ui.person.ViewPersonActivity
import com.farbodsz.familytree.ui.widget.TreeView
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : NavigationDrawerActivity(), PersonViewDialogFragment.OnDialogActionChosenListener {

    companion object {

        private const val LOG_TAG = "TreeActivity"

        private const val FRAGMENT_TAG_DIALOG = "dialog"

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

        /**
         * Request code for starting [CreatePersonActivity] for result, to create a parent for a
         * [Person].
         */
        private const val REQUEST_ADD_PARENT = 10

        /**
         * Request code for starting [EditMarriageActivity] for result, to create a new
         * [com.farbodsz.familytree.model.Marriage] involving a [Person].
         */
        private const val REQUEST_ADD_MARRIAGE = 11

        /**
         * Request code for starting [CreatePersonActivity] for result, to create a child for a
         * [Person].
         */
        private const val REQUEST_ADD_CHILD = 12

        /**
         * Intent extra key for supplying a [Person] to this activity. This will be used as the
         * root of the tree.
         */
        const val EXTRA_PERSON = "extra_person"

        /**
         * Intent extra key for supplying the forename of a person to show on the page's title.
         * It can be null, in which case a default/generic title will be shown.
         */
        const val EXTRA_NAME = "extra_name"
    }

    /**
     * Helper class for setting up the tree and showing it in the UI.
     */
    private lateinit var treeHandler: TreeHandler

    /**
     * The most recent [Person] selected from the tree.
     */
    private var selectedPersonCache: Person? = null

    /**
     * Whether any modifications have been made on this page (such as adding a new person).
     * This is used to determine what result should be sent to the calling activity.
     *
     * @see sendResult
     */
    private var hasModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootPerson = intent.extras?.getParcelable<Person>(EXTRA_PERSON)
        val personName = intent.extras?.getString(EXTRA_NAME)

        setupNavigation(rootPerson)
        setupTitle(rootPerson, personName)

        initTreeHandler()

        val rootNode = treeHandler.getDisplayedTree(rootPerson)
        treeHandler.updateTree(rootNode)
    }

    private fun setupNavigation(rootPerson: Person?) {
        // If a particular person is being displayed, then the nav drawer doesn't need to be shown
        @LayoutRes val layout = R.layout.activity_tree
        if (rootPerson == null) setContentView(withNavigation(layout)) else setContentView(layout)
    }

    private fun setupTitle(rootPerson: Person?, personName: String?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        updateToolbarTitle(personName)
        rootPerson?.let { supportActionBar!!.setDisplayHomeAsUpEnabled(true) }
    }

    private fun updateToolbarTitle(personName: String?) {
        val titleText = if (personName != null) {
            getString(R.string.title_tree_person, personName)
        } else {
            getString(R.string.title_tree)
        }
        supportActionBar!!.title = titleText
    }

    private fun initTreeHandler() {
        val treeContainer = findViewById<ViewGroup>(R.id.container)

        treeHandler = TreeHandler(this, treeContainer) { _, person ->
            selectedPersonCache = person
            val dialogFragment = PersonViewDialogFragment.newInstance(person, this)
            dialogFragment.show(supportFragmentManager, FRAGMENT_TAG_DIALOG)
        }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE, findViewById(R.id.toolbar))

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_tree, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_add -> addPerson()
            R.id.action_choose_layers -> chooseLayersDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = sendResult()

    /**
     * Starts activity for result for creating a new [Person].
     */
    private fun addPerson() {
        val intent = Intent(this, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_PERSON_CREATE)
    }

    /**
     * Displays a dialog allowing the user to choose the number of layers shown on the tree.
     * If there is no tree being displayed, a [Snackbar] error message will be shown instead.
     */
    private fun chooseLayersDialog() {
        val rootNode = treeHandler.updateRootNode()
        if (rootNode == null) {
            val layoutRoot = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
            Snackbar.make(layoutRoot, R.string.error_no_tree_no_layers, Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_choose_layers_title)
                .setItems(getNodeLayers(rootNode)) { _, which ->
                    val newDisplayedHeight = which + 1 // which is the index
                    treeHandler.updateTree(rootNode, newDisplayedHeight)
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    dialog.dismiss()
                }
        dialog = builder.show()
    }

    /**
     * Returns an array of strings containing the integers from 1 to N inclusive (N may be 1), where
     * N is the height of the given [node] (i.e. total number of layers).
     *
     * @see chooseLayersDialog
     */
    private fun <T> getNodeLayers(node: TreeNode<T>) = Array(node.height()) { i ->
        val layerNum = i + 1
        resources.getQuantityString(R.plurals.dialog_choose_layers_item, layerNum, layerNum)
    }

    /**
     * Sends the correct result back to where this activity was invoked from, and finishes the
     * activity.
     *
     * An "ok" result will be used if the tree has been modified, otherwise a "cancelled" result.
     *
     * @see android.app.Activity.RESULT_OK
     * @see android.app.Activity.RESULT_CANCELED
     */
    private fun sendResult() {
        if (hasModified) { // TODO what is this really for?
            val rootPerson = treeHandler.currentRootNode!!.data
            Log.d(LOG_TAG, "Sending successful result: $rootPerson")
            val returnIntent = Intent().putExtra(EXTRA_PERSON, rootPerson)
            setResult(Activity.RESULT_OK, returnIntent)
        } else {
            Log.d(LOG_TAG, "Sending cancelled result")
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(LOG_TAG, "onActivityResult called with requestCode=$requestCode, " +
                "resultCode=$resultCode")

        when (requestCode) {
            REQUEST_PERSON_CREATE -> if (resultCode == Activity.RESULT_OK) {
                // Refresh tree layout
                hasModified = true
                treeHandler.updateTree()
            }

            REQUEST_VIEW_PERSON -> if (resultCode == Activity.RESULT_OK) {
                // A person could be modified by starting EditPersonActivity from ViewPersonActivity
                hasModified = true
                treeHandler.updateTree()
            }

            REQUEST_ADD_PARENT -> if (resultCode == Activity.RESULT_OK) {
                val parent = data?.getParcelableExtra<Person>(CreatePersonActivity.EXTRA_PERSON)
                if (parent != null) {
                    val relationship = ChildRelationship(parent.id, selectedPersonCache!!.id)
                    ChildrenManager(this).add(relationship)
                    treeHandler.updateTree()
                } else {
                    Log.w(LOG_TAG, "Could not update tree - parent received was null")
                }
            }

            REQUEST_ADD_MARRIAGE -> if (resultCode == Activity.RESULT_OK) {
                // Marriage data already added in EditMarriageActivity since we passed
                // EXTRA_WRITE_DATA true - only update the calling activity
                treeHandler.updateTree()
            }

            REQUEST_ADD_CHILD -> if (resultCode == Activity.RESULT_OK) {
                val child = data?.getParcelableExtra<Person>(CreatePersonActivity.EXTRA_PERSON)
                if (child != null) {
                    val relationship = ChildRelationship(selectedPersonCache!!.id, child.id)
                    ChildrenManager(this).add(relationship)
                    treeHandler.updateTree()
                } else {
                    Log.w(LOG_TAG, "Could not update tree - child received was null")
                }
            }

            else -> Log.w(LOG_TAG, "Request code ($requestCode) not recognised")
        }
    }

    override fun onViewPerson(person: Person) {
        selectedPersonCache = person
        val intent = Intent(this, ViewPersonActivity::class.java)
                .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
        startActivityForResult(intent, REQUEST_VIEW_PERSON)
    }

    override fun onAddParent(person: Person) {
        val intent = Intent(this, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_PARENT)
    }

    override fun onAddMarriage(person: Person) {
        val intent = Intent(this, EditMarriageActivity::class.java)
                .putExtra(EditMarriageActivity.EXTRA_WRITE_DATA, true)
                .putExtra(EditMarriageActivity.EXTRA_EXISTING_PERSON, person)
        startActivityForResult(intent, REQUEST_ADD_MARRIAGE)
    }

    override fun onAddChild(person: Person) {
        val intent = Intent(this, CreatePersonActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_CHILD)
    }

    override fun onSwitchTree(person: Person) {
        // TODO
    }

    /**
     * Returns a tree containing the given [person].
     */
    private fun findTreeContainingPerson(person: Person): TreeNode<Person> {
        val rootPerson = ChildrenManager(this).getRootParent(person.id)
        return ChildrenManager(this).getTree(rootPerson.id)
    }

}
