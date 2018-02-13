package co.familytreeapp.data.query

/**
 * A collection of static helper functions for creating or combining filters.
 *
 * @see Filter
 * @see Query
 */
object Filters {

    @JvmStatic
    fun equal(property: String, value: String)= Filter("$property = $value")

    @JvmStatic
    fun and(filter1: Filter, filter2: Filter, vararg moreFilters: Filter) =
            joinFilters("AND", filter1, filter2, *moreFilters)

    @JvmStatic
    fun or(filter1: Filter, filter2: Filter, vararg moreFilters: Filter) =
            joinFilters("OR", filter1, filter2, *moreFilters)

    @JvmStatic
    private fun joinFilters(sqlKeyword: String, filter1: Filter, filter2: Filter,
                            vararg moreFilters: Filter): Filter {
        var sql: String = "${filter1.sqlStatement} $sqlKeyword ${filter2.sqlStatement}"

        for ((sqlStatement) in moreFilters) {
            sql = "$sql $sqlKeyword $sqlStatement"
        }

        return Filter(sql)
    }

}
