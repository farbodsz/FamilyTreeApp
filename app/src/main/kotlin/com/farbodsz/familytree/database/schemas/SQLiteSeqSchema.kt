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
