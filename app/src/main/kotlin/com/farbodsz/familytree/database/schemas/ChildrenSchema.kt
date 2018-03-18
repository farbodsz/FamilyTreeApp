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
     * @see com.farbodsz.familytree.database.DatabaseHelper
     */
    const val SQL_CREATE = "CREATE TABLE $TABLE_NAME($COL_PARENT_ID INTEGER NOT NULL, " +
            "$COL_CHILD_ID INTEGER NOT NULL)"

}
