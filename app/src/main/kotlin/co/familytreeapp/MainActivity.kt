package co.familytreeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
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

    private fun getDummyTree(): TreeNode<Person> {  // returns root node
        val root = TreeNode(Person(1, "Grandparent", "X", Gender.MALE))

        val parent1 = TreeNode(Person(2, "Parent 1", "X", Gender.MALE))
        val child1 = TreeNode(Person(3, "Child 1", "X", Gender.FEMALE))
        val child2 = TreeNode(Person(4, "Child 2", "X", Gender.MALE))
        child2.addChild(TreeNode(Person(5, "Grandchild 1", "X", Gender.FEMALE)))
        val child3 = TreeNode(Person(6, "Child 2", "X", Gender.FEMALE))
        parent1.addChildren(listOf(child1, child2, child3))

        val parent2 = TreeNode(Person(7, "Parent 2", "X", Gender.FEMALE))
        parent2.addChild(TreeNode(Person(8, "Child 4", "X", Gender.MALE)))

        val parent3 = TreeNode(Person(9, "Parent 3", "X", Gender.MALE))

        root.addChildren(listOf(parent1, parent2, parent3))
        return root
    }

}
