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

package com.farbodsz.familytree.database.manager

import android.content.Context
import android.util.Log
import com.farbodsz.familytree.DataNotFoundException
import com.farbodsz.familytree.MultipleIdResultsException
import com.farbodsz.familytree.database.query.Filters
import com.farbodsz.familytree.database.query.Query
import com.farbodsz.familytree.model.DataRelationship

/**
 * An abstract data manager class, containing methods which "relationship" data managers should use.
 *
 * @see DataManager
 */
abstract class RelationshipManager<T : DataRelationship>(
        context: Context
) : DataManager<T>(context) {

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
     */
    fun get(idPair: Pair<Int, Int>): T {
        val idQuery = Query.Builder()
                .addFilter(Filters.equal(idColumnNames.first, idPair.first.toString()))
                .addFilter(Filters.equal(idColumnNames.second, idPair.second.toString()))
                .build()
        val results = query(idQuery)
        return when (results.count()) {
            0 -> throw DataNotFoundException("DataRelationship", idPair)
            1 -> results[0]
            else -> throw MultipleIdResultsException("DataRelationship", idPair, results.count())
        }
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
        val query = Query.Builder()
                .addFilter(Filters.equal(idColumnNames.first, idPair.first.toString()))
                .addFilter(Filters.equal(idColumnNames.second, idPair.second.toString()))
                .build()
        Log.d(LOG_TAG, "Deleting using id pair: (${idPair.first}, ${idPair.second})...")
        delete(query)
    }

    /**
     * Deletes an item of type [T] with specified [idPair] and any other references to it, if any.
     * This function should be overridden by subclasses of [RelationshipManager] to specify which
     * references should be deleted.
     *
     * @see delete
     */
    open fun deleteWithReferences(idPair: Pair<Int, Int>) {
        delete(idPair)
    }

}
