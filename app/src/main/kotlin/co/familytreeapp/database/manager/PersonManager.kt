package co.familytreeapp.database.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import co.familytreeapp.DataNotFoundException
import co.familytreeapp.MultipleIdResultsException
import co.familytreeapp.database.DatabaseHelper
import co.familytreeapp.database.query.Filters
import co.familytreeapp.database.query.Query
import co.familytreeapp.database.schemas.ChildrenSchema
import co.familytreeapp.database.schemas.PersonsSchema
import co.familytreeapp.database.schemas.SQLiteSeqSchema
import co.familytreeapp.model.Person

/**
 * Contains some methods to perform CRUD operations for the *persons* table.
 */
class PersonManager(private val context: Context) : StandardDataManager<Person>(context) {

    companion object {
        private const val LOG_TAG = "PersonManager"
    }

    override val tableName = PersonsSchema.TABLE_NAME

    override val idColumn = PersonsSchema.COL_ID

    override fun createFromCursor(cursor: Cursor) = Person.from(cursor)

    /**
     * Returns the [Person] with the specified [id] from the database.
     *
     * @throws DataNotFoundException if the item with the [id] could not be found
     */
    fun get(id: Int): Person {
        val results = query(Query(Filters.equal(PersonsSchema.COL_ID, id.toString())))
        return when (results.count()) {
            0 -> throw DataNotFoundException("Person", id)
            1 -> results[0]
            else -> throw MultipleIdResultsException("Person", id, results.count())
        }
    }

    override fun propertiesAsContentValues(item: Person) = ContentValues().apply {
        put(PersonsSchema.COL_ID, item.id)
        put(PersonsSchema.COL_FORENAME, item.forename)
        put(PersonsSchema.COL_SURNAME, item.surname)
        put(PersonsSchema.COL_GENDER_ID, item.gender.id)
        put(PersonsSchema.COL_BIRTH_DATE_DAY, item.dateOfBirth.dayOfMonth)
        put(PersonsSchema.COL_BIRTH_DATE_MONTH, item.dateOfBirth.monthValue)
        put(PersonsSchema.COL_BIRTH_DATE_YEAR, item.dateOfBirth.year)
        put(PersonsSchema.COL_PLACE_OF_BIRTH, item.placeOfBirth)
        put(PersonsSchema.COL_DEATH_DATE_DAY, item.dateOfDeath?.dayOfMonth)
        put(PersonsSchema.COL_DEATH_DATE_MONTH, item.dateOfDeath?.monthValue)
        put(PersonsSchema.COL_DEATH_DATE_YEAR, item.dateOfDeath?.year)
        put(PersonsSchema.COL_PLACE_OF_DEATH, item.placeOfDeath)
    }

    /**
     * Returns the next available integer to use for the auto-incrementing primary key.
     */
    fun nextAvailableId(): Int {
        val db = DatabaseHelper.getInstance(context).readableDatabase
        val cursor = db.query(
                SQLiteSeqSchema.TABLE_NAME,
                null,
                SQLiteSeqSchema.COL_NAME + "=?",
                arrayOf(tableName),
                null, null, null)

        val highestId = if (cursor.count == 0) {
            0
        } else {
            cursor.moveToFirst()
            cursor.getInt(cursor.getColumnIndex(SQLiteSeqSchema.COL_SEQ))
        }
        cursor.close()

        val nextAvailId = highestId + 1
        Log.v(LOG_TAG, "Next available id found is $nextAvailId")

        return nextAvailId
    }

    override fun deleteWithReferences(id: Int) {
        super.deleteWithReferences(id)

        Log.d(LOG_TAG, "Deleting person (id: $id) and references to it")

        // Delete associated relationships, but not the other person in those relationships
        MarriagesManager(context).deleteMarriages(id)
        ChildrenManager(context).delete(Query.Builder()
                .addFilter(Filters.equal(ChildrenSchema.COL_PARENT_ID, id.toString()))
                .addFilter(Filters.equal(ChildrenSchema.COL_CHILD_ID, id.toString()))
                .build(Filters.JoinType.OR)
        )
    }

}
