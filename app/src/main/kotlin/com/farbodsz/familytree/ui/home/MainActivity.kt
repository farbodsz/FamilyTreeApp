package com.farbodsz.familytree.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.person.PersonListActivity
import com.farbodsz.familytree.ui.tree.TreeActivity
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation

/**
 * The main screen of the app.
 */
class MainActivity : NavigationDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_main))

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupTiles()
    }

    private fun setupTiles() {
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = HomeTileAdapter(this@MainActivity, getHomeTiles())
        }
    }

    private fun getHomeTiles(): List<HomeTile> {
        val viewTreeTile = ViewTreeTile(this) { goToPage(HomeTiles.VIEW_TREE) }

        val peopleCount = PersonManager(this).count()
        val peopleTile = PeopleTile(this, peopleCount) { goToPage(HomeTiles.PEOPLE) }

        return arrayListOf(viewTreeTile, peopleTile)
    }

    /**
     * Starts an activity determined by the [tile], and finishes this activity.
     */
    private fun goToPage(tile: HomeTiles) {
        val cls = when (tile) {
            HomeTiles.VIEW_TREE -> TreeActivity::class.java
            HomeTiles.PEOPLE -> PersonListActivity::class.java
        }
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_MAIN, findViewById(R.id.toolbar))

}
