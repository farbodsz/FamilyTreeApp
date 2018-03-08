package com.farbodsz.familytree.database.manager

import android.content.Context
import android.util.Log
import com.farbodsz.familytree.database.query.Filters
import com.farbodsz.familytree.database.query.Query
import com.farbodsz.familytree.model.StandardData

/**
 * An abstract data manager class which includes methods that all "standard" data managers should
 * inherit/implement.
 *
 * @see DataManager
 */
abstract class StandardDataManager<T : StandardData>(context: Context) : DataManager<T>(context) {

    companion object {
        private const val LOG_TAG = "StandardDataManager"
    }

    /**
     * The name of the unique (primary key) ID column of the table the subclass is managing.
     */
    abstract val idColumn: String

    /**
     * Updates an item of type [T] with [oldItemId], replacing it with the new [item].
     *
     * @see add
     * @see delete
     */
    fun update(oldItemId: Int, item: T) {
        delete(oldItemId)
        add(item)
    }

    /**
     * Deletes an item of type [T] with specified [id] from the table named [tableName].
     *
     * @see deleteWithReferences
     */
    private fun delete(id: Int) {
        val query = Query(Filters.equal(idColumn, id.toString()))
        Log.d(LOG_TAG, "Deleting items with id: $id...")
        delete(query)
    }

    /**
     * Deletes an item of type [T] with specified [id] and any other references to it.
     * This function should be overridden by subclasses of [StandardDataManager] to specify which
     * references should be deleted.
     *
     * @see delete
     */
    open fun deleteWithReferences(id: Int) {
        delete(id)
    }

}
