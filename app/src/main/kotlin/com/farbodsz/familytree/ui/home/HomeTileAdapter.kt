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

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farbodsz.familytree.R

/**
 * A [RecyclerView] adapter for displaying tiles containing information for the home screen.
 *
 * @property tiles  the list of [HomeTile]s to display
 */
class HomeTileAdapter(
        private val tiles: List<HomeTile>
) : RecyclerView.Adapter<HomeTileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_home_tile, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val tile = tiles[position]

        with(holder!!) {
            titleText.text = tile.title
            descriptionText.text = tile.description
            cardView.setCardBackgroundColor(tile.color)
        }
    }

    override fun getItemCount() = tiles.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val titleText: TextView = itemView.findViewById(R.id.text_title)
        val descriptionText: TextView = itemView.findViewById(R.id.text_desc)

        init {
            itemView.setOnClickListener {
                val tile = tiles[layoutPosition]
                tile.onClick.invoke(it)
            }
        }

    }

}
