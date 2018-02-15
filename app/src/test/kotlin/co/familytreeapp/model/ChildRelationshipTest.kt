package co.familytreeapp.model

import org.junit.Assert.fail
import org.junit.Test

/**
 * Unit tests for [ChildRelationship].
 */
class ChildRelationshipTest {

    @Test(expected = IllegalArgumentException::class)
    fun requirePositiveIds() {
        ChildRelationship(0, 2)
        fail("Expected an IllegalArgumentException to be thrown since parentId < 1")

        ChildRelationship(6, -7)
        fail("Expected an IllegalArgumentException to be thrown since childId < 1")
    }

}
