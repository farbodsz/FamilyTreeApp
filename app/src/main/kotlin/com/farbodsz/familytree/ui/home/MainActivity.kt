/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farbodsz.familytree.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.ui.NavigationDrawerActivity
import com.farbodsz.familytree.ui.event.EventsActivity
import com.farbodsz.familytree.ui.person.CreatePersonActivity
import com.farbodsz.familytree.ui.person.PersonListActivity
import com.farbodsz.familytree.ui.tree.TreeActivity
import com.farbodsz.familytree.util.standardNavigationParams
import com.farbodsz.familytree.util.withNavigation

/**
 * The main screen of the app.
 */
class MainActivity : NavigationDrawerActivity() {

    companion object {
        private const val REQUEST_NEW_ACTIVITY = 1
    }

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
            adapter = HomeTileAdapter(getHomeTiles())
        }
    }

    private fun getHomeTiles(): List<HomeTile> {
        val viewTreeTile = ViewTreeTile(this) { goToPage(HomeTiles.VIEW_TREE) }

        val peopleCount = PersonManager(this).count()
        val peopleTile = PeopleTile(this, peopleCount) { goToPage(HomeTiles.PEOPLE) }

        val eventsTile = EventsTile(this) { goToPage(HomeTiles.EVENTS) }

        val addPersonTile = AddPersonTile(this) { goToPage(HomeTiles.ADD_PERSON) }

        return arrayListOf(viewTreeTile, peopleTile, eventsTile, addPersonTile)
    }

    /**
     * Starts an activity determined by the [tile].
     */
    private fun goToPage(tile: HomeTiles) {
        val cls = when (tile) {
            HomeTiles.VIEW_TREE -> TreeActivity::class.java
            HomeTiles.PEOPLE -> PersonListActivity::class.java
            HomeTiles.EVENTS -> EventsActivity::class.java
            HomeTiles.ADD_PERSON -> CreatePersonActivity::class.java
        }
        val intent = Intent(this, cls)
        startActivityForResult(intent, REQUEST_NEW_ACTIVITY)
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_MAIN, findViewById(R.id.toolbar))

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_NEW_ACTIVITY && resultCode == Activity.RESULT_OK) {
            setupTiles() // refresh
        }
    }

}
