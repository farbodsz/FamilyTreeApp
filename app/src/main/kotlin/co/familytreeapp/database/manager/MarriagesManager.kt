package co.familytreeapp.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import co.familytreeapp.database.schemas.MarriagesSchema
import co.familytreeapp.model.Marriage

/**
 * Responsible for performing CRUD operations for the "marriages" table.
 */
class MarriagesManager(context: Context) : RelationshipManager<Marriage>(context) {

    companion object {
        private const val LOG_TAG = "MarriagesManager"
    }

    override val tableName = MarriagesSchema.TABLE_NAME

    override val idColumnNames = Pair(MarriagesSchema.COL_ID_1, MarriagesSchema.COL_ID_2)

    override fun createFromCursor(cursor: Cursor) = Marriage.from(cursor)

    override fun propertiesAsContentValues(item: Marriage) = ContentValues().apply {
        put(MarriagesSchema.COL_ID_1, item.person1Id)
        put(MarriagesSchema.COL_ID_2, item.person2Id)
        put(MarriagesSchema.COL_START_DATE_DAY, item.startDate.dayOfMonth)
        put(MarriagesSchema.COL_START_DATE_MONTH, item.startDate.monthValue)
        put(MarriagesSchema.COL_START_DATE_YEAR, item.startDate.year)
        put(MarriagesSchema.COL_END_DATE_DAY, item.endDate?.dayOfMonth)
        put(MarriagesSchema.COL_END_DATE_MONTH, item.endDate?.monthValue)
        put(MarriagesSchema.COL_END_DATE_YEAR, item.endDate?.year)
        put(MarriagesSchema.COL_PLACE_OF_MARRIAGE, item.placeOfMarriage)
    }

}
