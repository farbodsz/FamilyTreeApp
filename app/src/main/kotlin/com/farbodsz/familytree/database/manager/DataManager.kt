package com.farbodsz.familytree.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.farbodsz.familytree.database.DatabaseHelper
import com.farbodsz.familytree.database.query.Query
import com.farbodsz.familytree.model.DataModel

/**
 * The base data manager class, defining/implementing methods that *all* data managers should
 * inherit or implement.
 *
 * There are two main types of data managers in this app: "standard" and "relationship".
 *  - Standard data managers manage tables with a unique (primary key) integer ID column and extra
 *    columns of any type to store additional details of that object.
 *  - Relationship data managers manage tables with two integer ID columns that are not necessarily
 *    unique. These two columns each represent the unique ID of an object (with its details stored
 *    in a "standard" table); so overall, a row stores the relationship between two objects.
 *
 * @see StandardDataManager
 * @see RelationshipManager
 */
abstract class DataManager<T : DataModel>(private val context: Context) {

    companion object {
        private const val LOG_TAG = "DataManager"
    }

    /**
     * The name of the table the subclass is managing.
     */
    abstract val tableName: String

    /**
     * Query the table corresponding to the data manager subclass.
     *
     * @return a list of items that satisfy the table [query]
     */
    fun query(query: Query?): List<T> {
        val list = ArrayList<T>()

        val cursor = DatabaseHelper.getInstance(context).readableDatabase.query(
                tableName,
                null,
                query?.filter?.sqlStatement,
                null, null, null, null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            list.add(createFromCursor(cursor))
            cursor.moveToNext()
        }
        cursor.close()

        return list
    }

    /**
     * Constructs the data model class of type [T] using column values from the [cursor] provided.
     */
    abstract fun createFromCursor(cursor: Cursor): T

    fun getAll() = query(null)

    fun count() = getAll().count()

    /**
     * Adds an item of type [T] to the table named [tableName].
     */
    fun add(item: T) {
        val db = DatabaseHelper.getInstance(context).writableDatabase
        db.insert(tableName, null, propertiesAsContentValues(item))
        Log.d(LOG_TAG, "Added item $item")
    }

    /**
     * Puts properties of the data model type into [ContentValues], returning this.
     *
     * @see add
     */
    abstract fun propertiesAsContentValues(item: T): ContentValues

    /**
     * Deletes items returned by the [query] from the table named [tableName].
     */
    fun delete(query: Query) {
        val db = DatabaseHelper.getInstance(context).writableDatabase
        val deletedRows = db.delete(
                tableName,
                query.filter.sqlStatement,
                null
        )
        Log.d(LOG_TAG, "Deleted $deletedRows items from $tableName")
    }

}
