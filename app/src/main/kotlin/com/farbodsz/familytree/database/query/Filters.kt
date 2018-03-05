package com.farbodsz.familytree.database.query

/**
 * A collection of static helper functions for creating or combining filters.
 *
 * @see Filter
 * @see Query
 */
object Filters {

    /**
     * Keywords that can be used to specify how filters can be combined together.
     */
    enum class JoinType(val sqlKeyword: String) {
        AND("AND"),
        OR("OR")
    }

    @JvmStatic
    fun equal(property: String, value: String)= Filter("$property = $value")

    @JvmStatic
    fun notEqual(property: String, value: String)= Filter("$property != $value")

    @JvmStatic
    fun and(filter1: Filter, filter2: Filter, vararg moreFilters: Filter) =
            joinFilters("AND", filter1, filter2, *moreFilters)

    @JvmStatic
    fun or(filter1: Filter, filter2: Filter, vararg moreFilters: Filter) =
            joinFilters("OR", filter1, filter2, *moreFilters)

    @JvmStatic
    internal fun joinFilters(sqlKeyword: String, filter1: Filter, filter2: Filter,
                            vararg moreFilters: Filter): Filter {
        var sql = "${filter1.sqlStatement} $sqlKeyword ${filter2.sqlStatement}"

        for ((sqlToAdd) in moreFilters) {
            sql = "$sql $sqlKeyword $sqlToAdd"
        }

        return Filter(sql)
    }

}
