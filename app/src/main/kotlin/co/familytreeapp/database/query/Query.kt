package co.familytreeapp.database.query

import android.util.Log

/**
 * Helps for producing SQL queries on the database/
 */
class Query(val filter: Filter) {

    class Builder {

        companion object {
            private const val LOG_TAG = "Query.Builder"
        }

        private val filters: ArrayList<Filter> = ArrayList()

        /**
         * Add a filter to the SQL queryTable
         * @return the queryTable builder, so methods can be chained
         */
        fun addFilter(filter: Filter): Builder {
            filters.add(filter)
            Log.v(LOG_TAG, "Added filter: ${filter.sqlStatement}")
            return this
        }

        private fun combineFilters(): Filter {
            when (filters.size) {
                0 -> throw IllegalStateException("you must add at least one filter to the queryTable builder")
                1 -> return filters[0]
                2 -> return Filters.and(filters[0], filters[1])
                else -> {
                    // We need to make an array excluding the first two elements for the vararg
                    val moreFilters = filters.slice(IntRange(2, filters.size - 1)).toTypedArray()

                    return Filters.and(filters[0], filters[1], *moreFilters)
                }
            }
        }

        fun build() = Query(combineFilters())
    }
}
