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

package com.farbodsz.familytree.model

import android.database.Cursor
import com.farbodsz.familytree.database.schemas.ChildrenSchema

/**
 * Represents a single relationship between a parent and its child.
 *
 * @property parentId   the person ID of the parent
 * @property childId    the person ID of the child
 *
 * @see Person.id
 */
data class ChildRelationship(val parentId: Int, val childId: Int): DataRelationship {

    init {
        require(parentId > 0 && childId > 0) {
            "the ids must be greater than 0 (currently: parentId=$parentId, childId=$childId)"
        }

        require(parentId != childId) {
            "the ids cannot be the same (currently: parentId=$parentId, childId=$childId)"
        }
    }

    companion object {

        /**
         * Instantiates a [ChildRelationship] object by getting values in columns from a [cursor].
         */
        @JvmStatic fun from(cursor: Cursor) = ChildRelationship(
                cursor.getInt(cursor.getColumnIndex(ChildrenSchema.COL_PARENT_ID)),
                cursor.getInt(cursor.getColumnIndex(ChildrenSchema.COL_CHILD_ID))
        )
    }

    override fun getIds() = Pair(parentId, childId)

}
