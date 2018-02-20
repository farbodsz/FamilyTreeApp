package co.familytreeapp.database.manager

import android.content.Context
import android.util.Log
import co.familytreeapp.database.DatabaseHelper
import co.familytreeapp.database.query.Filters
import co.familytreeapp.database.query.Query
import co.familytreeapp.model.DataRelationship

/**
 * An abstract data manager class, containing methods which "relationship" data managers should use.
 *
 * @see DataManager
 */
abstract class RelationshipManager<T : DataRelationship>(
        private val context: Context
): DataManager<T>(context) {

    companion object {
        private const val LOG_TAG = "RelationshipManager"
    }

    /**
     * The names of the ID columns that defines the relationship of the table the subclass is
     * managing.
     *
     * The order of the column name pair must correspond to the order of the ID pair in
     * [DataRelationship.getIds].
     */
    abstract val idColumnNames: Pair<String, String>

    /**
     * Returns the item of type [T] with the specified [idPair] from the table named [tableName].
     *
     * @see getWithFirstId
     * @see getWithSecondId
     */
    fun get(idPair: Pair<Int, Int>): T {
        val idQuery = Query.Builder()
                .addFilter(Filters.equal(idColumnNames.first, idPair.first.toString()))
                .addFilter(Filters.equal(idColumnNames.second, idPair.second.toString()))
                .build()
        val results = query(idQuery)
        if (results.size != 1) {
            Log.e(LOG_TAG, "Query using ids: $idPair returned ${results.size} results instead of 1")
        }
        return results[0]
    }

    /**
     * Updates an item of type [T] with [oldItemIdPair], replacing it with the new [item].
     *
     * @see add
     * @see delete
     */
    fun update(oldItemIdPair: Pair<Int, Int>, item: T) {
        delete(oldItemIdPair)
        add(item)
    }

    /**
     * Deletes relationships ([T]) with specified [idPair] from the table named [tableName].
     *
     * The order of this pair must correspond to that of [idColumnNames].
     */
    fun delete(idPair: Pair<Int, Int>) {
        val db = DatabaseHelper.getInstance(context).writableDatabase
        db.delete(
                tableName,
                "${idColumnNames.first}=? AND ${idColumnNames.second}=?",
                arrayOf(idPair.first.toString(), idPair.second.toString())
        )
        Log.d(LOG_TAG, "Deleted item (id1: ${idPair.first}, id2: ${idPair.second})")
    }

    /**
     * Deletes relationships ([T]) with the specified *first* ID from the table named [tableName].
     *
     * This is the first ID from [DataRelationship.getIds] corresponding to that of [idColumnNames].
     */
    fun deleteFirstIds(firstId: Int) {
        val db = DatabaseHelper.getInstance(context).writableDatabase
        db.delete(
                tableName,
                "${idColumnNames.first}=?",
                arrayOf(firstId.toString())
        )
        Log.d(LOG_TAG, "Deleted item (id1: $firstId)")
    }

}
