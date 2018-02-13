package co.familytreeapp.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import co.familytreeapp.R
import co.familytreeapp.util.standardNavigationParams
import co.familytreeapp.util.withNavigation

/**
 * Activity for displaying the tree as a vertical list with indents.
 */
class TreeListActivity : NavigationDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_list))

        setupFab()

        // TODO show list
    }

    private fun setupFab() {
        val addPersonButton = findViewById<FloatingActionButton>(R.id.fab)
        addPersonButton.setOnClickListener {
            val intent = Intent(this@TreeListActivity, EditPersonActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE_LIST, findViewById(R.id.toolbar))

}
