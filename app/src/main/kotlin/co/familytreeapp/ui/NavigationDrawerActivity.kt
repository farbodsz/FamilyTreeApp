package co.familytreeapp.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import co.familytreeapp.R
import co.familytreeapp.ui.person.PersonListActivity

/**
 * Data class to contain details about navigation in a subclass of [NavigationDrawerActivity].
 *
 * @property navigationItem an integer indicating which navigation item the activity is used for
 * @property drawerLayout   the [DrawerLayout] used for navigation in the activity's layout
 * @property navigationView the [NavigationView] used for navigation in the activity's layout
 * @property toolbar        the [Toolbar] used as part of navigation in the activity's layout
 *
 * @see NavigationDrawerActivity.getSelfNavigationParams
 */
data class NavigationParameters(
        val navigationItem: Int,
        val drawerLayout: DrawerLayout,
        val navigationView: NavigationView,
        val toolbar: Toolbar
)

/**
 * An activity used for the implementing navigation drawer behaviour.
 * This should be implemented by activities which contain a navigation drawer.
 */
abstract class NavigationDrawerActivity : AppCompatActivity() {

    companion object {

        const val NAVDRAWER_ITEM_MAIN = R.id.nav_item_home
        const val NAVDRAWER_ITEM_TREE = R.id.nav_item_tree
        const val NAVDRAWER_ITEM_TREE_LIST = R.id.nav_item_tree_list
        const val NAVDRAWER_ITEM_PERSON_LIST = R.id.nav_item_person_list

        const val NAVDRAWER_ITEM_INVALID = -1

        private const val NAVDRAWER_LAUNCH_DELAY = 250L
    }

    /**
     * Holds details about navigation behaviour in a subclass of this activity.
     */
    private lateinit var navParams: NavigationParameters

    private lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * This method should be overridden in subclasses of [NavigationDrawerActivity] to supply
     * details about the navigation behaviour.
     */
    abstract fun getSelfNavigationParams(): NavigationParameters

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        navParams = getSelfNavigationParams()
        setupLayout()
    }

    private fun setupLayout() {
        setSupportActionBar(navParams.toolbar)

        with(navParams.navigationView) {
            menu.findItem(navParams.navigationItem).isChecked = true

            setNavigationItemSelectedListener({ menuItem ->
                handleNavigationSelection(menuItem)
                true
            })
        }

        drawerToggle = ActionBarDrawerToggle(
                this,
                navParams.drawerLayout,
                navParams.toolbar,
                R.string.drawer_open,
                R.string.drawer_close)

        navParams.drawerLayout.addDrawerListener(drawerToggle)

        drawerToggle.syncState()
    }

    private fun handleNavigationSelection(menuItem: MenuItem) {
        if (menuItem.itemId == navParams.navigationItem) {
            navParams.drawerLayout.closeDrawers()
            return
        }

        // Launch the target Activity after a short delay, to allow the close animation to play
        val handler = Handler()
        handler.postDelayed({ goToNavDrawerItem(menuItem.itemId) }, NAVDRAWER_LAUNCH_DELAY)

        if (menuItem.isCheckable) {
            navParams.navigationView.menu.findItem(navParams.navigationItem).isChecked = false
            menuItem.isChecked = true
        }

        navParams.drawerLayout.closeDrawers()
    }

    private fun goToNavDrawerItem(menuItem: Int) {
        val intent = when (menuItem) {
            NAVDRAWER_ITEM_MAIN -> Intent(this, MainActivity::class.java)
            NAVDRAWER_ITEM_TREE -> Intent(this, TreeActivity::class.java)
            NAVDRAWER_ITEM_TREE_LIST -> Intent(this, TreeListActivity::class.java)
            NAVDRAWER_ITEM_PERSON_LIST -> Intent(this, PersonListActivity::class.java)
            else -> throw IllegalArgumentException("unrecognised menu item: $menuItem")
        }

        startActivity(intent)
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    override fun onBackPressed() = if (navParams.drawerLayout.isDrawerOpen(GravityCompat.START)) {
        navParams.drawerLayout.closeDrawer(GravityCompat.START)
    } else {
        super.onBackPressed()
    }

}
