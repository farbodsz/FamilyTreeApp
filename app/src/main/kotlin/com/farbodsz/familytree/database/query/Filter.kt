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

package com.farbodsz.familytree.database.query

/**
 * Stores part of a SQLite selection statement, used to filter results in database queries.
 *
 * @see Filters
 * @see Query
 */
data class Filter(var sqlStatement: String) {

    init {
        // Surround the SQL command in parenthesis to avoid mix ups when combined with other
        // filters.
        sqlStatement = "($sqlStatement)"
    }

}
