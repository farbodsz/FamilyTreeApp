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
