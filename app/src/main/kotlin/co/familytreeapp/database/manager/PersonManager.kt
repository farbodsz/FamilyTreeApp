package co.familytreeapp.database.manager

import android.content.ContentValues
import android.content.Context
import co.familytreeapp.database.DatabaseHelper
import co.familytreeapp.database.query.Filters
import co.familytreeapp.database.query.Query
import co.familytreeapp.database.schemas.PersonsSchema
import co.familytreeapp.model.Person

/**
 * Contains some methods to perform CRUD operations for the *persons* table.
 */
class PersonManager(private val context: Context) : DataManager<Person>(context) {

    override val tableName = PersonsSchema.TABLE_NAME

    override val idColumn = PersonsSchema.COL_ID

    override fun query(query: Query?): List<Person> {
        val list = ArrayList<Person>()

        val cursor = DatabaseHelper.getInstance(context).readableDatabase.query(
                PersonsSchema.TABLE_NAME,
                null,
                query?.filter?.sqlStatement,
                null, null, null, null
        )
        cursor.moveToFirst()
        list.add(Person.from(cursor))
        cursor.close()

        return list
    }

    /**
     * Returns the [Person] with the specified [id] from the database.
     */
    fun get(id: Int) = query(Query(Filters.equal(PersonsSchema.COL_ID, id.toString())))[0]

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

}
