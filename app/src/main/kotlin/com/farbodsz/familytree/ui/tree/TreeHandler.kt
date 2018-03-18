/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farbodsz.familytree.ui.tree

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.farbodsz.familytree.database.manager.ChildrenManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode
import com.farbodsz.familytree.ui.widget.PersonView
import com.farbodsz.familytree.ui.widget.TreeView
import com.farbodsz.familytree.util.OnDataClick

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

        /**
         * Returns a tree to be displayed in the UI.
         */
        @JvmStatic
        fun getDisplayedTree(context: Context, person: Person?) = if (person == null) {
            getFullTree(context)
        } else {
            ChildrenManager(context).getTree(person.id)
        }

        /**
         * Returns a tree consisting of all people added in the database.
         * The root of the tree is taken as the node with greatest height.
         *
         * @return the root node of the tree, or null if there is no tree to display
         */
        @JvmStatic
        private fun getFullTree(context: Context): TreeNode<Person>? {
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
    }

    /**
     * The root node of the tree currently being displayed.
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
     * @see Companion.getDisplayedTree
     */
    fun getDisplayedTree(person: Person?) = TreeHandler.getDisplayedTree(context, person)

}
