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
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.person.CreatePersonActivity
import com.farbodsz.familytree.ui.widget.TreeView
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation


/**
 * Activity to display a [TreeView].
 */
class TreeActivity : NavigationDrawerActivity(), PersonViewDialogFragment.OnTreeChangedListener {

    companion object {

        private const val LOG_TAG = "TreeActivity"

        private const val FRAGMENT_TAG_DIALOG = "dialog"

        private const val REQUEST_DIALOG_PERSON_VIEW = "request_personViewDialogFragment"

        /**
         * Request code for starting [CreatePersonActivity] for result.
         */
        private const val REQUEST_PERSON_CREATE = 8

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
     * The [Person] who's portion of the family tree is being displayed. It will be the root of the
     * tree.
     *
     * This can be null if the whole tree is being displayed.
     */
    private var rootPerson: Person? = null

    /**
     * Whether any modifications have been made on this page (such as adding a new person).
     * This is used to determine what result should be sent to the calling activity.
     *
     * @see sendResult
     */
    private var hasModified = false

    /**
     * Reference to the most recent [PersonViewDialogFragment] displayed from this activity.
     */
    private var dialogFragment: PersonViewDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootPerson = intent.extras?.getParcelable(EXTRA_PERSON)
        val personName = intent.extras?.getString(EXTRA_NAME)
        setupNavigation()

        setupTitle(personName)

        initTreeHandler()
        val rootNode = treeHandler.getDisplayedTree(rootPerson)
        treeHandler.displayTree(rootNode)
    }

    private fun setupNavigation() {
        // If a particular person is being displayed, then the nav drawer doesn't need to be shown
        @LayoutRes val layout = R.layout.activity_tree
        if (rootPerson == null) setContentView(withNavigation(layout)) else setContentView(layout)
    }

    private fun setupTitle(personName: String?) {
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
            dialogFragment = PersonViewDialogFragment.newInstance(person).apply {
                show(supportFragmentManager, FRAGMENT_TAG_DIALOG)
            }
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
        val rootNode = treeHandler.getDisplayedTree(rootPerson)
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
                    treeHandler.displayTree(rootNode, newDisplayedHeight)
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

        if (requestCode == REQUEST_PERSON_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refresh tree layout
                hasModified = true
                treeHandler.displayTree(null)
            }
            return
        }

        // For other request codes, pass to PersonViewDialogFragment to handle
        dialogFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTreeUpdate() {
        Log.v(LOG_TAG, "onTreeUpdate called")
        // Updates the tree using the existing root node

        // rootPerson cannot be null when onTreeUpdate is called, since nothing would be in the tree
        // to be clicked on and update
        val rootNode = ChildrenManager(this).getTree(rootPerson!!.id)

        hasModified = true
        treeHandler.displayTree(rootNode)
    }

    override fun onTreeChangeRoot(newRootNode: TreeNode<Person>, newName: String) {
        Log.v(LOG_TAG, "onTreeChangeRoot called with rootNode=$newRootNode, name=$newName")

        rootPerson = newRootNode.data
        treeHandler.displayTree(newRootNode)

        updateToolbarTitle(newName)
    }

}
