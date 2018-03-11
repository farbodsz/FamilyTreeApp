package com.farbodsz.familytree.ui.tree

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.farbodsz.familytree.database.manager.ChildrenManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Gender
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.ui.widget.PersonView
import com.farbodsz.familytree.ui.widget.TreeView
import com.farbodsz.familytree.util.OnDataClick
import org.threeten.bp.LocalDate

/**
 * Manages setting up tree data and displaying it in the UI.
 *
 * @property context        activity [Context]
 * @property treeContainer  the [ViewGroup] that will contain the [TreeView]
 * @property onPersonClick  actions that will be invoked when a [PersonView] in the tree has been
 *                          clicked.
 */
class TreeHandler(
        private val context: Context,
        private val treeContainer: ViewGroup,
        private val onPersonClick: OnDataClick<Person>
) {

    companion object {
        private const val LOG_TAG = "TreeHandler"
        private const val DISPLAY_DUMMY_TREE = false // for debugging purposes only
    }

    /**
     * The root node currently being used in the tree being displayed.
     * This is null if no tree is being displayed.
     */
    var currentRootNode: TreeNode<Person>? = null
        private set

    /**
     * The number of layers of the tree currently being displayed.
     * This is null if no tree is being displayed, or if the whole tree is being displayed (i.e not
     * restricted to a particular displayed height).
     */
    var currentDisplayedHeight: Int? = null
        private set

    /**
     * Updates and displays the tree in the UI, reflecting any changes made in the database.
     *
     * @see updateRootNode
     * @see displayTree
     */
    fun updateTree() {
        updateRootNode()
        displayTree()
    }

    /**
     * Updates the [currentRootNode] reflecting any changes made in the database.
     *
     * @return the updated [currentRootNode]
     * @see updateTree
     */
    fun updateRootNode(): TreeNode<Person>? {
        val currentRootPerson = currentRootNode?.data ?: return null // otherwise nothing to update

        currentRootNode = getDisplayedTree(currentRootPerson) // retrieve again

        return currentRootNode
    }

    /**
     * Updates and displays a tree in the UI, using the given [rootNode] with [displayedHeight]
     * number of layers (from the root) being shown.
     *
     * @param rootNode          the root node of the tree being displayed. It can be null if there
     *                          is no tree to display.
     * @param displayedHeight   the number of layers of the tree to show. This can be null to show
     *                          the whole tree, rather than a portion of it.
     *
     * @see displayTree
     */
    fun updateTree(rootNode: TreeNode<Person>?, displayedHeight: Int? = null) {
        currentRootNode = rootNode
        currentDisplayedHeight = displayedHeight
        displayTree()
    }

    /**
     * Displays a tree in the UI using the [currentRootNode] and [currentDisplayedHeight].
     *
     * @see updateTree
     */
    private fun displayTree() {
        Log.v(LOG_TAG, "displayTree using currentRootNode=$currentRootNode, " +
                "currentDisplayedHeight=$currentDisplayedHeight")

        val rootNode = currentRootNode ?: return // don't display anything if rootNode=null

        val treeView = TreeView(context)
        treeView.setTreeSource(rootNode, currentDisplayedHeight)
        treeView.onPersonViewClick = onPersonClick

        treeContainer.apply {
            removeAllViews()
            addView(treeView)
        }
    }

    /**
     * Returns a tree to be displayed in the UI.
     *
     * @param person    the person who's tree will be displayed. It can be null to indicate that a
     *                  full tree (consisting of all/most people from the db) should be displayed.
     *
     * @see getFullTree
     * @see ChildrenManager.getTree
     */
    fun getDisplayedTree(person: Person?) =
            if (person == null) getFullTree() else ChildrenManager(context).getTree(person.id)

    /**
     * Returns a tree consisting of all people added in the database.
     * The root of the tree is taken as the node with greatest height.
     *
     * @return the root node of the tree, or null if there is no tree to display
     */
    private fun getFullTree(): TreeNode<Person>? {
        if (DISPLAY_DUMMY_TREE) {
            return getDummyTree()
        }

        val allPeople = PersonManager(context).getAll()
        if (allPeople.isEmpty()) {
            return null
        }

        val childrenManager = ChildrenManager(context)

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

}
