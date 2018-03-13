package com.farbodsz.familytree.ui.person

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.widget.PersonCircleImageView
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import com.farbodsz.familytree.util.OnDataClick

/**
 * A [RecyclerView] adapter for displaying [people] in a standard list layout.
 */
class PersonAdapter(
        private val people: List<Person>
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private var onItemClickAction: OnDataClick<Person>? = null

    fun onItemClick(action: OnDataClick<Person>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_person, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val person = people[position]

        with(holder!!) {
            nameText.text = person.fullName
            infoText.text = person.dateOfBirth.format(DATE_FORMATTER_BIRTH)
            personImageView.person = person
        }
    }

    override fun getItemCount() = people.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val personImageView: PersonCircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val infoText: TextView = itemView.findViewById(R.id.text2)

        init {
            itemView.setOnClickListener {
                val position = layoutPosition
                onItemClickAction?.invoke(it, people[position])
            }
        }

    }

}
