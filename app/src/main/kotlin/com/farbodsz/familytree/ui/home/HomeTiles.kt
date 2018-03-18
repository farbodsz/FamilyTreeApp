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

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import com.farbodsz.familytree.R
import com.farbodsz.familytree.util.OnClick

enum class HomeTiles {
    VIEW_TREE,
    PEOPLE,
    EVENTS,
    ADD_PERSON
}

/**
 * Holds data about a tile being displayed on the main page.
 */
sealed class HomeTile(
        val title: String,
        val description: String,
        @ColorInt val color: Int,
        val onClick: OnClick
)

class ViewTreeTile(context: Context, onClick: OnClick) : HomeTile(
        context.getString(R.string.home_item_tree_title),
        context.getString(R.string.home_item_tree_desc),
        ContextCompat.getColor(context, R.color.homeCard_tree_bkgd),
        onClick
)

class PeopleTile(context: Context, peopleCount: Int, onClick: OnClick) : HomeTile(
        context.getString(R.string.home_item_people_title),
        context.resources.getQuantityString(R.plurals.home_item_people_desc, peopleCount, peopleCount),
        ContextCompat.getColor(context, R.color.homeCard_people_bkgd),
        onClick
)

class EventsTile(context: Context, onClick: OnClick) : HomeTile(
        context.getString(R.string.home_item_events_title),
        context.getString(R.string.home_item_events_desc),
        ContextCompat.getColor(context, R.color.homeCard_events_bkgd),
        onClick
)

class AddPersonTile(context: Context, onClick: OnClick) : HomeTile(
        context.getString(R.string.home_item_addPerson_title),
        context.getString(R.string.home_item_addPerson_desc),
        ContextCompat.getColor(context, R.color.homeCard_addPerson_bkgd),
        onClick
)
