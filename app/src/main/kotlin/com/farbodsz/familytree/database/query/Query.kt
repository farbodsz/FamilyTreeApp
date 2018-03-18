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

package com.farbodsz.familytree.database.query

import android.util.Log

/**
 * Convenience class for producing SQLite queries.
 */
class Query(val filter: Filter) {

    class Builder {

        companion object {
            private const val LOG_TAG = "Query.Builder"
        }

        private val filters: ArrayList<Filter> = ArrayList()

        /**
         * Adds a filter to the SQL query builder to be joined with AND
         * @return the query [Builder], to allow method chaining
         */
        fun addFilter(filter: Filter): Builder {
            filters.add(filter)
            return this
        }

        /**
         * @see build
         */
        private fun combineFilters(joinType: Filters.JoinType): Filter {
            val filter = when (filters.size) {
                0 -> throw IllegalStateException("you must add at least one filter to the Builder")
                1 -> filters[0]
                2 -> Filters.joinFilters(joinType.sqlKeyword, filters[0], filters[1])
                else -> {
                    // We need to make an array excluding the first two elements for the vararg
                    val moreFilters = filters.slice(IntRange(2, filters.size - 1)).toTypedArray()

                    Filters.joinFilters(joinType.sqlKeyword, filters[0], filters[1], *moreFilters)
                }
            }
            Log.v(LOG_TAG, "Combined filters: $filter")
            return filter
        }

        /**
         * Combines the added filters together using the [joinType] specified.
         */
        fun build(joinType: Filters.JoinType = Filters.JoinType.AND) =
                Query(combineFilters(joinType))
    }
}
