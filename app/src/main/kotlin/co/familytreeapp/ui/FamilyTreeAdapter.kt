package co.familytreeapp.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.model.Person
import co.familytreeapp.model.TreeListItem
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Type definition for an action to be preformed when a view in the list has been clicked.
 *
 * This is a function type with its parameters as the view that was clicked and the
 * [layout position][RecyclerView.ViewHolder.getLayoutPosition] of the ViewHolder. The function does
 * not return anything.
 */
typealias OnItemClick = (view: View, position: Int) -> Unit

/**
 * A [RecyclerView] adapter for displaying [Person]s in a vertical tree.
 */
class FamilyTreeAdapter(
        private val context: Context,
        private val treeItems: List<TreeListItem<Person>>
) : RecyclerView.Adapter<FamilyTreeAdapter.ViewHolder>() {

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
        val person = treeItem.data

        with(holder!!) {
            linearLayout.layoutParams = calculateItemLayoutParams(treeItem.depth)
            nameText.text = person.toString()
            infoText.text = "Date of birth" // TODO

            imageView.borderColor = ContextCompat.getColor(context, if (person.gender.isMale()) {
                R.color.image_border_male
            } else {
                R.color.image_border_female
            })
        }
    }

    /**
     * Calculates an appropriate left margin for an item in the vertical tree, depending on its
     * depth in the tree.
     *
     * @return layout parameters containing data about the calculated margins
     */
    private fun calculateItemLayoutParams(depth: Int): LayoutParams {
        val leftMargin = context.dpToPx(48) * depth
        val newLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        newLayoutParams.setMargins(leftMargin, 0, 0, 0)
        return newLayoutParams
    }

    override fun getItemCount() = treeItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
        val imageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val infoText: TextView = itemView.findViewById(R.id.text2)

        init {
            itemView.setOnClickListener { onItemClickAction?.invoke(it, layoutPosition) }
        }

    }

}
