package com.farbodsz.familytree.model

/**
 * Interface that "relationship" model classes should implement.
 *
 * @see co.familytree.database.manager.RelationshipManager
 */
interface DataRelationship : DataModel {

    /**
     * Returns the pair of the IDs used to define the relationship.
     */
    fun getIds(): Pair<Int, Int>

}
