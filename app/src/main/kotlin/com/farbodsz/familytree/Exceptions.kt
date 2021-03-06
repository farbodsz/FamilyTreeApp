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

package com.farbodsz.familytree

/**
 * A [DataNotFoundException] is thrown when an item cannot be found in the database.
 *
 * @param msg   exception message
 */
class DataNotFoundException(msg: String) : Exception(msg) {

    constructor(className: String, itemId: Int) :
            this("Could not find $className with id: $itemId")

    constructor(className: String, itemIdPair: Pair<Int, Int>) :
            this("Could not find $className with ids: $itemIdPair")
}

/**
 * A [MultipleIdResultsException] is thrown when an unexpected number of rows are returned by a
 * database query for a unique id, and so the data is unable to be processed.
 *
 * @param msg   exception message
 */
class MultipleIdResultsException(msg: String) : Exception(msg) {

    constructor(className: String, itemId: Int, resultCount: Int) :
            this("Expected 1 result of $className with id: $itemId; found $resultCount")

    constructor(className: String, itemIdPair: Pair<Int, Int>, resultCount: Int) :
            this("Expected 1 result of $className with id: $itemIdPair; found $resultCount")

}
