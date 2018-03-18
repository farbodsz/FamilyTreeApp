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

package com.farbodsz.familytree.database.schemas

/**
 * Contains the names of the columns used by SQLite's internal table ('sqlite_sequence') for
 * auto-incrementing.
 *
 * See the [SQLite docs 'sqlite_sequence'](http://sqlite.org/fileformat2.html#seqtab).
 */
object SQLiteSeqSchema {

    // These values should NOT be changed - these are the names used by the internal table.

    const val TABLE_NAME = "sqlite_sequence"
    const val COL_NAME = "name"
    const val COL_SEQ = "seq"

}
