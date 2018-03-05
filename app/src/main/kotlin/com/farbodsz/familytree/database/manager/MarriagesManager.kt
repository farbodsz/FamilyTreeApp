package com.farbodsz.familytree.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.farbodsz.familytree.database.query.Filters
import com.farbodsz.familytree.database.query.Query
import com.farbodsz.familytree.database.schemas.MarriagesSchema
import com.farbodsz.familytree.model.Marriage

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

    /**
     * Returns the list of marriages (former and current) of a person with the given [personId]
     */
    fun getMarriages(personId: Int): List<Marriage> {
        val marriagesQuery = Query.Builder()
                .addFilter(Filters.equal(MarriagesSchema.COL_ID_1, personId.toString()))
                .addFilter(Filters.equal(MarriagesSchema.COL_ID_2, personId.toString()))
                .build(Filters.JoinType.OR)
        return query(marriagesQuery)
    }

    /**
     * Updates the list of a person's marriages.
     *
     * @param personId  the person ID of one of the people involved in the marriage
     * @param marriages the list of marriages that will replace the old list in the database
     */
    fun updateMarriages(personId: Int, marriages: List<Marriage>) {
        Log.d(LOG_TAG, "Updating marriages")

        // Delete the current marriages then add the new list
        // (This is easier than comparing the db version with the list given in the parameter to see
        // which need to be deleted/added/kept the same)
        deleteMarriages(personId)
        for (m in marriages) add(m)
    }

    /**
     * Deletes all marriages involving person with [personId].
     */
    fun deleteMarriages(personId: Int) {
        val query = Query.Builder()
                .addFilter(Filters.equal(MarriagesSchema.COL_ID_1, personId.toString()))
                .addFilter(Filters.equal(MarriagesSchema.COL_ID_2, personId.toString()))
                .build(Filters.JoinType.OR)
        delete(query)
    }

}
