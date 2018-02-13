package co.familytreeapp.ui

import android.os.Bundle
import co.familytreeapp.R
import co.familytreeapp.util.standardNavigationParams
import co.familytreeapp.util.withNavigation

/**
 * Activity for displaying the tree as a vertical list with indents.
 */
class TreeListActivity : NavigationDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_tree_list))

        // TODO
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_TREE_LIST, findViewById(R.id.toolbar))

}