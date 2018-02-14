package co.familytreeapp.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import co.familytreeapp.database.schemas.ChildrenSchema
import co.familytreeapp.model.ChildRelationship

/**
 * Responsible for performing CRUD operations for the "children" table.
 */
class ChildrenManager(context: Context) : RelationshipManager<ChildRelationship>(context) {

    override val tableName = ChildrenSchema.TABLE_NAME

    override val idColumnNames = Pair(ChildrenSchema.COL_PARENT_ID, ChildrenSchema.COL_CHILD_ID)

    override fun createFromCursor(cursor: Cursor) = ChildRelationship.from(cursor)

    override fun propertiesAsContentValues(item: ChildRelationship) = ContentValues().apply {
        put(ChildrenSchema.COL_PARENT_ID, item.parentId)
        put(ChildrenSchema.COL_CHILD_ID, item.childId)
    }

}
