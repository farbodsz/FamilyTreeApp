package co.familytreeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import co.familytreeapp.model.TreeListItem
import co.familytreeapp.ui.FamilyTreeAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayTestTree()
    }

    private fun displayTestTree() {
        val items = getDummyItems()

        val vertTreeAdapter = FamilyTreeAdapter(this, items)
        vertTreeAdapter.onItemClick { _, position ->
            Toast.makeText(this, "${items[position].data} clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = vertTreeAdapter
        }
    }

    private fun getDummyItems(): List<TreeListItem<String>> {
        val list = ArrayList<TreeListItem<String>>()
        with(list) {
            add(TreeListItem("Grandparent", 0))
            add(TreeListItem("Parent 1", 1))
            add(TreeListItem("Child 1", 2))
            add(TreeListItem("Child 2", 2))
            add(TreeListItem("Grandchild 1", 3))
            add(TreeListItem("Child 3", 2))
            add(TreeListItem("Parent 2", 1))
            add(TreeListItem("Child 4", 2))
            add(TreeListItem("Parent 3", 1))
        }
        return list
    }

}
