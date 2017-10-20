import co.familytreeapp.model.TreeNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for [TreeNode].
 */
class TreeTest {

    @Test
    fun childNodeParentUpdatedWhenAddedToNode() {
        val node = TreeNode("Parent")
        node.addChild(TreeNode("Child"))
        val child = node.getChildren()[0]

        assertNotNull(child.parent)
        assertEquals(child.parent!!.data, "Parent")
    }

}
