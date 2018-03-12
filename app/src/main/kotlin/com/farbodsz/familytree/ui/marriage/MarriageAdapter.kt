package com.farbodsz.familytree.ui.marriage

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Marriage
import com.farbodsz.familytree.ui.widget.PersonCircleImageView
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import com.farbodsz.familytree.util.OnDataClick

/**
 * A [RecyclerView] adapter for displaying [marriages] in a standard list layout.
 */
class MarriageAdapter(
        private val context: Context,
        private val personId: Int,
        private val marriages: List<Marriage>
) : RecyclerView.Adapter<MarriageAdapter.ViewHolder>() {

    private var onItemClickAction: OnDataClick<Marriage>? = null

    fun onItemClick(action: OnDataClick<Marriage>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_person, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val marriage = marriages[position]
        val spouseId = marriage.getOtherSpouseId(personId)
        val spouse = PersonManager(context).get(spouseId)

        with(holder!!) {
            nameText.text = spouse.fullName
            infoText.text = marriage.startDate.format(DATE_FORMATTER_BIRTH)
            personImageView.person = spouse
        }
    }

    override fun getItemCount() = marriages.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val personImageView: PersonCircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val infoText: TextView = itemView.findViewById(R.id.text2)

        init {
            itemView.setOnClickListener {
                val position = layoutPosition
                onItemClickAction?.invoke(it, marriages[position])
            }
        }
    }

}
