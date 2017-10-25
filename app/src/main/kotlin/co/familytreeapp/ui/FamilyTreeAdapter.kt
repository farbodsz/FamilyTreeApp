package co.familytreeapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.model.TreeListItem

/**
 * Type definition for an action to be preformed when a view in the list has been clicked.
 *
 * This is a function type with its parameters as the view that was clicked and the
 * [layout position][RecyclerView.ViewHolder.getLayoutPosition] of the ViewHolder. The function does
 * not return anything.
 */
typealias OnItemClick = (view: View, position: Int) -> Unit

/**
 * A [RecyclerView] adapter for displaying items in a vertical tree.
 */
class FamilyTreeAdapter<T>(
        private val context: Context,
        private val treeItems: List<TreeListItem<T>>
) : RecyclerView.Adapter<FamilyTreeAdapter<T>.ViewHolder>() {

    private var onItemClickAction: OnItemClick? = null

    fun onItemClick(action: OnItemClick) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_tree_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val treeItem = treeItems[position]

        val leftMargin = context.dpToPx(48) * treeItem.depth
        val newLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        newLayoutParams.setMargins(leftMargin, 0, 0, 0)

        holder!!.textView.text = treeItem.data.toString()
        holder.textView.layoutParams = newLayoutParams
    }

    override fun getItemCount() = treeItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView.findViewById(R.id.textView)

        init {
            itemView.setOnClickListener { onItemClickAction?.invoke(it, layoutPosition) }
        }

    }

}
