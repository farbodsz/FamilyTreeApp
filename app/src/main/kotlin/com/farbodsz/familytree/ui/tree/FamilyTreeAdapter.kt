package com.farbodsz.familytree.ui.tree

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.model.tree.TreeListItem
import com.farbodsz.familytree.ui.widget.PersonCircleImageView
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import com.farbodsz.familytree.util.OnDataClick
import com.farbodsz.familytree.util.dpToPx

/**
 * A [RecyclerView] adapter for displaying [Person]s in a vertical tree.
 */
class FamilyTreeAdapter(
        private val context: Context,
        private val treeItems: List<TreeListItem<Person>>
) : RecyclerView.Adapter<FamilyTreeAdapter.ViewHolder>() {

    private var onItemClickAction: OnDataClick<TreeListItem<Person>>? = null

    fun onItemClick(action: OnDataClick<TreeListItem<Person>>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_person, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val treeItem = treeItems[position]
        val person = treeItem.data

        with(holder!!) {
            linearLayout.layoutParams = calculateItemLayoutParams(treeItem.depth)
            nameText.text = person.toString()
            infoText.text = person.dateOfBirth.format(DATE_FORMATTER_BIRTH)
            personImageView.person = person
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
        val personImageView: PersonCircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val infoText: TextView = itemView.findViewById(R.id.text2)

        init {
            itemView.setOnClickListener {
                val pos = layoutPosition
                onItemClickAction?.invoke(it, treeItems[pos])
            }
        }

    }

}
