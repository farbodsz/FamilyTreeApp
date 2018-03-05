package com.farbodsz.familytree.database.schemas

import com.farbodsz.familytree.model.Marriage

/**
 * Schema for the 'marriages' table.
 * It contains constants for the column names of the table, and SQLite statement for creation.
 *
 * This table does **not** contain a primary key: a composite primary key from the two ID columns
 * would not work if the same two people re-married; and a separate ID referring to the marriage
 * would have no purpose. Hence, there is no primary key and the correct row can be identified by
 * using the two ID columns present.
 *
 * @see Marriage
 */
object MarriagesSchema {

    const val TABLE_NAME = "marriages"

    const val COL_ID_1 = "person1_id"
    const val COL_ID_2 = "person2_id"

    const val COL_START_DATE_DAY = "startDate_dayOfMonth"
    const val COL_START_DATE_MONTH = "startDate_month"
    const val COL_START_DATE_YEAR = "startDate_year"

    const val COL_END_DATE_DAY = "endDate_dayOfMonth"
    const val COL_END_DATE_MONTH = "endDate_month"
    const val COL_END_DATE_YEAR = "endDate_year"

    const val COL_PLACE_OF_MARRIAGE = "placeOfMarriage"

    /**
     * SQLite statement which creates the "marriages" table upon execution.
     * @see co.familytree.database.DatabaseHelper
     */
    const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME($COL_ID_1 INTEGER NOT NULL, $COL_ID_2 INTEGER NOT NULL, " +
            "$COL_START_DATE_DAY INTEGER, $COL_START_DATE_MONTH INTEGER, $COL_START_DATE_YEAR INTEGER, " +
            "$COL_END_DATE_DAY INTEGER, $COL_END_DATE_MONTH INTEGER, $COL_END_DATE_YEAR INTEGER, " +
            "$COL_PLACE_OF_MARRIAGE TEXT)"

}
