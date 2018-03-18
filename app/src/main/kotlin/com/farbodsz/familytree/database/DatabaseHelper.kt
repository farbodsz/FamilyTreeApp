/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farbodsz.familytree.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.farbodsz.familytree.database.schemas.ChildrenSchema
import com.farbodsz.familytree.database.schemas.MarriagesSchema
import com.farbodsz.familytree.database.schemas.PersonsSchema

/**
 * Helper singleton class for managing the SQLite database.
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
