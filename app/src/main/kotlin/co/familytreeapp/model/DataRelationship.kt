package co.familytreeapp.model

/**
 * Interface that "relationship" model classes should implement.
 *
 * @see co.familytreeapp.database.manager.RelationshipManager
 */
interface DataRelationship : DataModel {

    /**
     * Returns the pair of the IDs used to define the relationship.
     */
    fun getIds(): Pair<Int, Int>

}
