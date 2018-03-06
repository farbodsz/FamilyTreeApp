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
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Gender
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.person.EditPersonActivity
import com.farbodsz.familytree.ui.person.ViewPersonActivity
import com.farbodsz.familytree.ui.widget.TreeView
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation
import org.threeten.bp.LocalDate

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : NavigationDrawerActivity() {

    companion object {

        private const val LOG_TAG = "TreeActivity"

        private const val DISPLAY_DUMMY_TREE = false // for debugging purposes only

        /**
         * Request code for starting [ViewPersonActivity] for result.
         */
        private const val REQUEST_PERSON_VIEW = 8

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        const val EXTRA_PERSON = "extra_person"
    }

    /**
     * The [rootNode] being used to display the family tree.
     * This can be null where [setupTree] has not been invoked, or there is no tree to display.
     */
    private var rootNode: TreeNode<Person>? = null

    /**
     * The [Person] who's portion of the family tree is being displayed.
     * This can be null if the whole tree is being displayed.
     */
    private var person: Person? = null

    private var hasModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        person = intent.extras?.getParcelable(EXTRA_PERSON)

        setupNavigation()
        setupTitle()
        setupTree()
    }

    private fun setupNavigation() {
        // If a particular person is being displayed, then the nav drawer doesn't need to be shown
        @LayoutRes val mainLayout = R.layout.activity_tree

        if (person == null) {
            setContentView(withNavigation(mainLayout))
        } else {
            setContentView(mainLayout)
        }
    }

    private fun setupTitle() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        person?.let {
            supportActionBar!!.title = getString(R.string.title_tree_person, it.forename)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * Shows the tree for the first time.
     */
    private fun setupTree() {
        val treeSource = getDisplayedTree()
        treeSource?.let {
            displayTree(it) // only display if source not null
        }
    }

    /**
     * Displays a tree using the [rootNode], with [displayedHeight] no. of layers (from the root).
     */
    private fun displayTree(rootNode: TreeNode<Person>, displayedHeight: Int? = null) {
        this.rootNode = rootNode

        val treeView = TreeView(this).apply {
            setTreeSource(rootNode, displayedHeight)
            onPersonViewClick = { person ->
                val intent = Intent(this@TreeActivity, ViewPersonActivity::class.java)
                        .putExtra(ViewPersonActivity.EXTRA_PERSON, person)
                startActivityForResult(intent, REQUEST_PERSON_VIEW)
            }
        }

        findViewById<ViewGroup>(R.id.container).apply {
            removeAllViews()
            addView(treeView)
        }
    }

    /**
     * Returns the tree to be displayed in the UI.
     */
    private fun getDisplayedTree() = if (person == null) {
        Log.v(LOG_TAG, "Displaying full tree")
        getFullTree()
    } else {
        Log.v(LOG_TAG, "Displaying tree for: $person")
        val childrenManager = ChildrenManager(this)
        childrenManager.getTree(person!!.id)
    }

    /**
     * Returns a tree consisting of all people added in the database.
     *
     * The root of the tree is taken as the node with greatest height.
     *
     * @return the root node of the tree, or null if there is no tree to display
     */
    private fun getFullTree(): TreeNode<Person>? {
        if (DISPLAY_DUMMY_TREE) {
            return getDummyTree()
        }

        val allPeople = PersonManager(this).getAll()
        if (allPeople.isEmpty()) {
            return null
        }

        val childrenManager = ChildrenManager(this)

        val nodes = ArrayList<TreeNode<Person>>()
        for (person in allPeople) {
            val n = childrenManager.getTree(person.id)
            nodes.add(n)
        }

        var greatestHeight = 0
        lateinit var nodeWithGreatestHeight: TreeNode<Person>
        for (node in nodes) {
            val height = node.height()
            if (height > greatestHeight) {
                greatestHeight = height
                nodeWithGreatestHeight = node
            }
        }

        return nodeWithGreatestHeight
    }

    private fun getDummyTree(): TreeNode<Person> {
        val dummyDate = LocalDate.now()

        val grandparent = Person(1, "Pedar", "Salamat-Zadeh", Gender.MALE, dummyDate, "", dummyDate, "")
        val parent1 = Person(2, "Farzaneh", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val parent2 = Person(3, "Naser", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val parent3 = Person(4, "Farshad", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val parent4 = Person(5, "Forough", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val child1 = Person(6, "Aniseh", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child2 = Person(7, "Sana", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child3 = Person(8, "Ayeh", "Zeighami", Gender.FEMALE, dummyDate, "", dummyDate, "")
        val child4 = Person(9, "Ghazal", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child5 = Person(10, "Raouf", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child6 = Person(11, "Khaled", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child7 = Person(12, "Farbod", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child8 = Person(13, "Fardis", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val child9 = Person(14, "Asal", "Pourmashal", Gender.FEMALE, dummyDate, "", null, "")
        val child10 = Person(15, "Mehdi", "Pourmashal", Gender.MALE, dummyDate, "", null, "")
        val grandchild1 = Person(16, "Fatemeh", "???", Gender.FEMALE, dummyDate, "", null, "")
        val grandchild2 = Person(17, "Mohammad-Houssain", "???", Gender.MALE, dummyDate, "", null, "")

        return TreeNode(grandparent).apply { addChildren(listOf(
                TreeNode(parent1).apply { addChildren(listOf(
                        TreeNode(child1),
                        TreeNode(child2),
                        TreeNode(child3)
                )) },
                TreeNode(parent2).apply { addChildren(listOf(
                        TreeNode(child4).apply { addChildren(listOf(
                                TreeNode(grandchild1),
                                TreeNode(grandchild2)
                        )) },
                        TreeNode(child5),
                        TreeNode(child6)
                )) },
                TreeNode(parent3).apply { addChildren(listOf(
                        TreeNode(child7),
                        TreeNode(child8)
                )) },
                TreeNode(parent4).apply { addChildren(listOf(
                        TreeNode(child9),
                        TreeNode(child10)
                )) }
        )) }
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
            R.id.action_add -> startActivity(Intent(this, EditPersonActivity::class.java))
            R.id.action_choose_layers -> chooseLayersDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = sendResult()

    /**
     * Displays a dialog allowing the user to choose the number of layers shown on the tree.
     * If there is no tree being displayed, a [Snackbar] error message will be shown instead.
     */
    private fun chooseLayersDialog() {
        if (rootNode == null) {
            val layoutRoot = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
            Snackbar.make(layoutRoot, R.string.error_no_tree_no_layers, Snackbar.LENGTH_LONG)
                    .show()
            return
        }

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_choose_layers_title)
                .setItems(getNodeLayers(rootNode!!)) { _, which ->
                    val newDisplayedHeight = which + 1 // which is the index
                    displayTree(rootNode!!, newDisplayedHeight) // update tree
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
        if (hasModified) {
            Log.d(LOG_TAG, "Sending successful result: $person")
            val returnIntent = Intent().putExtra(EXTRA_PERSON, person)
            setResult(Activity.RESULT_OK, returnIntent)
        } else {
            Log.d(LOG_TAG, "Sending cancelled result")
            setResult(Activity.RESULT_CANCELED)
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERSON_VIEW) {
            // A person could be modified by starting EditPersonActivity from ViewPersonActivity

            if (resultCode == Activity.RESULT_OK) {
                // Refresh tree layout
                hasModified = true
                displayTree(rootNode!!, null)
            }
        }
    }

}
