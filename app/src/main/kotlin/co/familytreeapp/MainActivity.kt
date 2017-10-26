package co.familytreeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.FamilyTreeAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayTestTree()
    }

    private fun displayTestTree() {
        val root = getDummyTree()

        val items = root.asTreeList()

        val vertTreeAdapter = FamilyTreeAdapter(this, items)
        vertTreeAdapter.onItemClick { _, position ->
            Toast.makeText(this, "${items[position].data} clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vertTreeAdapter
        }
    }

    private fun getDummyTree(): TreeNode<String> {  // returns root node
        val root = TreeNode("Grandparent")

        val parent1 = TreeNode("Parent 1")
        val child1 = TreeNode("Child 1")
        val child2 = TreeNode("Child 2")
        child2.addChild(TreeNode("Grandchild 1"))
        val child3 = TreeNode("Child 2")
        parent1.addChildren(listOf(child1, child2, child3))

        val parent2 = TreeNode("Parent 2")
        parent2.addChild(TreeNode("Child 4"))

        val parent3 = TreeNode("Parent 3")

        root.addChildren(listOf(parent1, parent2, parent3))
        return root
    }

}
