package co.familytreeapp.model.tree

import org.junit.Assert
import org.junit.Test

/**
 * Unit tests for [TreeListItem]
 */
class TreeListItemTest {

    @Test(expected = IllegalArgumentException::class)
    fun requireDepthAtLeastZero() {
        TreeListItem("", -1)
        Assert.fail("Expected an IllegalArgumentException to be thrown since depth < 0")
    }

}
