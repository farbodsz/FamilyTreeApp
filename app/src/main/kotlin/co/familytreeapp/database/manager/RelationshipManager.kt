package co.familytreeapp.database.manager

import android.content.Context
import android.util.Log
import co.familytreeapp.database.DatabaseHelper
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

}
