package com.farbodsz.familytree.database.schemas

import com.farbodsz.familytree.model.Person

/**
 * Schema for the 'children' table. The table stores the parent-child relationships between people.
 *
 * This class contains constants for column names of the table, and SQLite statement for creation.
 * Neither of the two columns of this table are unique identifiers (see documentation), but they are
 * both non-null columns.
 */
object ChildrenSchema {

    const val TABLE_NAME = "children"

    /**
     * The person ID of the parent.
     *
     * This is not a unique column since parents can have more than one child, and so there would be
     * multiple rows with the same parent ID and different child IDs.
     *
     * @see COL_CHILD_ID
     * @see Person.id
     */
    const val COL_PARENT_ID = "parent_id"

    /**
     * The person ID of a child.
     *
     * This is not a unique column since children have two parents, so there would be two rows with
     * the same child ID and different parent IDs.
     *
     * @see COL_PARENT_ID
     * @see Person.id
     */
    const val COL_CHILD_ID = "child_id"

    /**
     * SQLite statement which creates the "children" table upon execution.
     * @see co.familytree.database.DatabaseHelper
     */
    const val SQL_CREATE = "CREATE TABLE $TABLE_NAME($COL_PARENT_ID INTEGER NOT NULL, " +
            "$COL_CHILD_ID INTEGER NOT NULL)"

}
