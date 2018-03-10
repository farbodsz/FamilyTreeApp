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
