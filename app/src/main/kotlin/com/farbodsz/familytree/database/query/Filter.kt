package com.farbodsz.familytree.database.query

/**
 * Stores part of a SQLite selection statement, used to filter results in database queries.
 *
 * @see Filters
 * @see Query
 */
data class Filter(var sqlStatement: String) {

    init {
        // Surround the SQL command in parenthesis to avoid mix ups when combined with other
        // filters.
        sqlStatement = "($sqlStatement)"
    }

}
