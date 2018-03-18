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

package com.farbodsz.familytree.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.farbodsz.familytree.database.query.Filters
import com.farbodsz.familytree.database.query.Query
import com.farbodsz.familytree.database.schemas.ChildrenSchema
import com.farbodsz.familytree.model.ChildRelationship
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeNode

/**
 * Responsible for performing CRUD operations for the "children" table.
 */
class ChildrenManager(
        private val context: Context
) : RelationshipManager<ChildRelationship>(context) {

    companion object {
        private const val LOG_TAG = "ChildrenManager"
    }

    override val tableName = ChildrenSchema.TABLE_NAME

    override val idColumnNames = Pair(ChildrenSchema.COL_PARENT_ID, ChildrenSchema.COL_CHILD_ID)

    override fun createFromCursor(cursor: Cursor) = ChildRelationship.from(cursor)

    override fun propertiesAsContentValues(item: ChildRelationship) = ContentValues().apply {
        put(ChildrenSchema.COL_PARENT_ID, item.parentId)
        put(ChildrenSchema.COL_CHILD_ID, item.childId)
    }

    /**
     * Adds a list of children to the parent.
     *
     * @param parentId  the person ID of the parent
     * @param children  the list of children that will be added to the current list, if existent
     */
    fun addChildren(parentId: Int, children: List<Person>) {
        Log.d(LOG_TAG, "Adding children")
        for (child in children) {
            val relationship = ChildRelationship(parentId, child.id)
            add(relationship)
        }
    }

    /**
     * Updates the list of the parent's children.
     *
     * @param parentId  the person ID of the parent
     * @param children  the list of children that will replace the old list in the database
     */
    fun updateChildren(parentId: Int, children: List<Person>) {
        Log.d(LOG_TAG, "Updating children")

        // Delete the current children then add the new list
        // (This is easier than comparing the db version with the list given in the parameter to see
        // which need to be deleted/added/kept the same)
        val query = Query(Filters.equal(ChildrenSchema.COL_PARENT_ID, parentId.toString()))
        delete(query)
        addChildren(parentId, children)
    }

    /**
     * Returns the list of children of a parent with the given [parentId]
     *
     * @see getParents
     */
    fun getChildren(parentId: Int): List<Person> {
        val childrenQuery = Query(Filters.equal(ChildrenSchema.COL_PARENT_ID, parentId.toString()))
        val relationships = query(childrenQuery)

        val personManager = PersonManager(context)
        val children = ArrayList<Person>()
        for (r in relationships) {
            val person = personManager.get(r.childId)
            children.add(person)
        }

        return children
    }

    /**
     * Returns the tree of the root [Person] and its children recursively.
     *
     * @param rootId    the person ID of the root of the tree
     * @return  the root node of the tree which contains its children, its children's children, etc.
     *
     * @see getChildren
     */
    fun getTree(rootId: Int): TreeNode<Person> {
        val personManager = PersonManager(context)
        val rootNode = TreeNode(personManager.get(rootId))

        for (child in getChildren(rootId)) {
            val childNode = getTree(child.id)
            rootNode.addChild(childNode)
        }

        return rootNode
    }

    /**
     * Returns the list of parents of a child with the given [childId]
     *
     * @see getChildren
     */
    fun getParents(childId: Int): List<Person> {
        val parentQuery = Query(Filters.equal(ChildrenSchema.COL_CHILD_ID, childId.toString()))
        val relationships = query(parentQuery)

        val personManager = PersonManager(context)
        val parents = ArrayList<Person>()
        for (r in relationships) {
            val person = personManager.get(r.parentId)
            parents.add(person)
        }

        return parents
    }

    /**
     * Returns the root [Person] of a tree containing a person with [personId].
     *
     * _N.B. there can be numerous possible roots, but only one will be returned here._
     *
     * @see getParents
     */
    fun getRootParent(personId: Int): Person {
        val parents = getParents(personId)
        return if (parents.isEmpty()) {
            // Has no parents, so this is the root
            PersonManager(context).get(personId)
        } else {
            val parentId = parents[0].id // use the first parent found as default
            getRootParent(parentId)
        }
    }

}
