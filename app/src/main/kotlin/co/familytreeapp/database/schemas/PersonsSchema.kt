package co.familytreeapp.database.schemas

import android.provider.BaseColumns
import co.familytreeapp.model.Person

/**
 * Schema for the 'persons' table.
 * It contains constants for the column names of the table, and SQLite statement for creation.
 *
 * _N.B. data for marriages is stored in a separate table (see [MarriagesSchema])_
 *
 * @see Person
 */
object PersonsSchema {

    const val TABLE_NAME = "persons"

    /** Name of the integer primary key column, corresponding to [Person.id] */
    const val COL_ID = BaseColumns._ID

    const val COL_FORENAME = "forename"
    const val COL_SURNAME = "surname"
    const val COL_GENDER_ID = "gender_id"

    const val COL_BIRTH_DATE_DAY = "dateOfBirth_dayOfMonth"
    const val COL_BIRTH_DATE_MONTH = "dateOfBirth_month"
    const val COL_BIRTH_DATE_YEAR = "dateOfBirth_year"
    const val COL_PLACE_OF_BIRTH = "placeOfBirth"

    const val COL_DEATH_DATE_DAY = "dateOfDeath_dayOfMonth"
    const val COL_DEATH_DATE_MONTH = "dateOfDeath_month"
    const val COL_DEATH_DATE_YEAR = "dateOfDeath_year"
    const val COL_PLACE_OF_DEATH = "placeOfDeath"

    /**
     * SQLite statement which creates the "persons" table upon execution.
     * @see co.familytreeapp.database.DatabaseHelper
     */
    const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "$COL_FORENAME TEXT NOT NULL, $COL_SURNAME TEXT NOT NULL, " +
            "$COL_GENDER_ID INTEGER NOT NULL, $COL_BIRTH_DATE_DAY INTEGER NOT NULL, " +
            "$COL_BIRTH_DATE_MONTH INTEGER NOT NULL, $COL_BIRTH_DATE_YEAR INTEGER NOT NULL, " +
            "$COL_PLACE_OF_BIRTH TEXT, $COL_DEATH_DATE_DAY INTEGER, $COL_DEATH_DATE_MONTH INTEGER " +
            "$COL_DEATH_DATE_YEAR INTEGER, $COL_PLACE_OF_DEATH TEXT)"

}
