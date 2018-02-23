package co.familytreeapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.familytreeapp.R
import co.familytreeapp.database.manager.ChildrenManager
import co.familytreeapp.model.Gender
import co.familytreeapp.model.Person
import co.familytreeapp.model.tree.TreeNode
import co.familytreeapp.ui.person.EditPersonActivity
import co.familytreeapp.ui.widget.TreeView
import co.familytreeapp.util.standardNavigationParams
import co.familytreeapp.util.withNavigation
import org.threeten.bp.LocalDate

/**
 * Activity to display a [TreeView].
 */
class TreeActivity : NavigationDrawerActivity() {

    companion object {

        private const val LOG_TAG = "TreeActivity"

        /**
         * Intent extra key for supplying a [Person] to this activity.
         */
        const val EXTRA_PERSON = "extra_person"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_tree))

        val person = intent.extras?.getParcelable<Person>(EXTRA_PERSON)

        val tree = if (person == null) {
            Log.v(LOG_TAG, "Displaying dummy tree")
            getDummyTree()

        } else {
            Log.v(LOG_TAG, "Displaying tree for: $person")

            val childrenManager = ChildrenManager(this)
            childrenManager.getTree(person.id)
        }

        val treeView = findViewById<TreeView>(R.id.treeView)
        treeView.setTreeSource(tree)
    }

    private fun getDummyTree(): TreeNode<Person> {
        val dummyDate = LocalDate.now()

        val grandparent = Person(1, "Pedar", "Salamat-Zadeh", Gender.MALE, dummyDate, "", dummyDate, "")
        val parent1 = Person(2, "Farzaneh", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val parent2 = Person(3, "Naser", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val parent3 = Person(4, "Farshad", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val parent4 = Person(5, "Forough", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val child1 = Person(6, "Aniseh", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child2 = Person(7, "Sana", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child3 = Person(8, "Ayeh", "Zeighami", Gender.FEMALE, dummyDate, "", dummyDate, "")
        val child4 = Person(9, "Ghazal", "Zeighami", Gender.FEMALE, dummyDate, "", null, "")
        val child5 = Person(10, "Raouf", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child6 = Person(11, "Khaled", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child7 = Person(12, "Farbod", "Salamat-Zadeh", Gender.MALE, dummyDate, "", null, "")
        val child8 = Person(13, "Fardis", "Salamat-Zadeh", Gender.FEMALE, dummyDate, "", null, "")
        val child9 = Person(14, "Asal", "Pourmashal", Gender.FEMALE, dummyDate, "", null, "")
        val child10 = Person(15, "Mehdi", "Pourmashal", Gender.MALE, dummyDate, "", null, "")
        val grandchild1 = Person(16, "Fatemeh", "???", Gender.FEMALE, dummyDate, "", null, "")
        val grandchild2 = Person(17, "Mohammad-Houssain", "???", Gender.MALE, dummyDate, "", null, "")

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
