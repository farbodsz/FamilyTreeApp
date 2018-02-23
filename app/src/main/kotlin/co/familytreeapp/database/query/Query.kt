package co.familytreeapp.database.query

import android.util.Log

/**
 * Convenience class for producing SQL queries.
 */
class Query(val filter: Filter) {

    class Builder {

        companion object {
            private const val LOG_TAG = "Query.Builder"
        }

        private val filters: ArrayList<Filter> = ArrayList()

        /**
         * Adds a filter to the SQL query builder to be joined with AND
         * @return the query builder, so methods can be chained
         */
        fun addFilter(filter: Filter): Builder {
            filters.add(filter)
            Log.v(LOG_TAG, "Added filter: ${filter.sqlStatement}")
            return this
        }

        /**
         * @see build
         */
        private fun combineFilters(joinType: Filters.JoinType) = when (filters.size) {
            0 -> throw IllegalStateException("you must add at least one filter to the Builder")
            1 -> filters[0]
            2 -> Filters.joinFilters(joinType.sqlKeyword, filters[0], filters[1])
            else -> {
                // We need to make an array excluding the first two elements for the vararg
                val moreFilters = filters.slice(IntRange(2, filters.size - 1)).toTypedArray()

                Filters.joinFilters(joinType.sqlKeyword, filters[0], filters[1], *moreFilters)
            }
        }

        /**
         * Combines the added filters together using the [joinType] specified.
         */
        fun build(joinType: Filters.JoinType = Filters.JoinType.AND) =
                Query(combineFilters(joinType))
    }
}
