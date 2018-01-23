package co.familytreeapp.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import co.familytreeapp.R
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.widget.TreeView

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : AppCompatActivity() {

    companion object {
        @JvmField val DUMMY_ROOT = TreeNode("Grandparent").apply { addChildren(listOf(
                TreeNode("Parent 1").apply { addChildren(listOf(
                        TreeNode("Child 1"),
                        TreeNode("Child 2").apply { addChildren(listOf(
                                TreeNode("Grandchild 1"),
                                TreeNode("Grandchild 2")
                        )) },
                        TreeNode("Child 3")
                )) },
                TreeNode("Parent 2").apply { addChildren(listOf(
                        TreeNode("Child 4").apply { addChildren(listOf(
                                TreeNode("Grandchild 3"),
                                TreeNode("Grandchild 4"),
                                TreeNode("Grandchild 5"),
                                TreeNode("Grandchild 6")
                        )) }
                )) },
                TreeNode("Parent 3")
        )) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree)

        Log.d("TreeActivity", DUMMY_ROOT.toStringRecursive())

        val treeView = findViewById<TreeView<String>>(R.id.treeView)
        treeView.setTreeSource(DUMMY_ROOT, 4)
    }

}
