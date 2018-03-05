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
