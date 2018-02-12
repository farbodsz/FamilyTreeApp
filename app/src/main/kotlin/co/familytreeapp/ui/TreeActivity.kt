package co.familytreeapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import co.familytreeapp.R
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
import co.familytreeapp.model.TreeNode
import co.familytreeapp.ui.widget.TreeView
import org.threeten.bp.LocalDate

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : NavigationDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_tree))

        val dummyTree = getDummyTree()

        val treeView = findViewById<TreeView>(R.id.treeView)
        treeView.setTreeSource(dummyTree)
    }

    // TODO: temporary data
    private fun getDummyTree(): TreeNode<Person> {
        val dummyDate = LocalDate.now()

        val grandparent = Person(1, "Pedar", "Salamat-Zadeh", Gender.MALE, dummyDate, "", dummyDate, "", emptyList())
        val parent1 = Person(2, "Farzaneh", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val parent2 = Person(3, "Naser", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "", emptyList())
        val parent3 = Person(4, "Farshad", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "", emptyList())
        val parent4 = Person(5, "Forough", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child1 = Person(6, "Aniseh", "Zeighami", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child2 = Person(7, "Sana", "Zeighami", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child3 = Person(8, "Ayeh", "Zeighami", Gender.FEMALE, dummyDate, "", dummyDate, "", emptyList())
        val child4 = Person(9, "Ghazal", "Zeighami", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child5 = Person(10, "Raouf", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "", emptyList())
        val child6 = Person(11, "Khaled", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "", emptyList())
        val child7 = Person(12, "Farbod", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "", emptyList())
        val child8 = Person(13, "Fardis", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child9 = Person(14, "Asal", "Pourmashal", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val child10 = Person(15, "Mehdi", "Pourmashal", Gender.MALE, dummyDate, "", null, "", emptyList())
        val grandchild1 = Person(16, "Fatemeh", "???", Gender.FEMALE, dummyDate, "", null, "", emptyList())
        val grandchild2 = Person(17, "Mohammad-Houssain", "???", Gender.MALE, dummyDate, "", null, "", emptyList())

        return TreeNode(grandparent).apply { addChildren(listOf(
                TreeNode(parent1).apply { addChildren(listOf(
                        TreeNode(child1),
                        TreeNode(child2),
                        TreeNode(child3)
                )) },
                TreeNode(parent2).apply { addChildren(listOf(
                        TreeNode(child4).apply { addChildren(listOf(
                                TreeNode(grandchild1),
                                TreeNode(grandchild2)
                        )) },
                        TreeNode(child5),
                        TreeNode(child6)
                )) },
                TreeNode(parent3).apply { addChildren(listOf(
                        TreeNode(child7),
                        TreeNode(child8)
                )) },
                TreeNode(parent4).apply { addChildren(listOf(
                        TreeNode(child9),
                        TreeNode(child10)
                )) }
        )) }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE, findViewById(R.id.toolbar))

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_tree, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> startActivity(Intent(this, EditPersonActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

}
