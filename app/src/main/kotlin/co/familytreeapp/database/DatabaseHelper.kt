package co.familytreeapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import co.familytreeapp.database.schemas.ChildrenSchema
import co.familytreeapp.database.schemas.MarriagesSchema
import co.familytreeapp.database.schemas.PersonsSchema

/**
 * Helper class for managing the SQLite database.
 */
class DatabaseHelper private constructor(context: Context): SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
) {

    companion object {

        private const val LOG_TAG = "DatabaseHelper"

        private const val DATABASE_NAME = "FamilyTreeDatabase.db"
        private const val DATABASE_VERSION = 1

        private var instance: DatabaseHelper? = null

        @JvmStatic fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(LOG_TAG, "onCreate() invoked")

        with(db!!) {
            execSQL(PersonsSchema.SQL_CREATE)
            execSQL(ChildrenSchema.SQL_CREATE)
            execSQL(MarriagesSchema.SQL_CREATE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i(LOG_TAG, "onUpgrade() invoked with oldVersion $oldVersion and newVersion $newVersion")

        throw IllegalArgumentException() // not implemented
    }

}
