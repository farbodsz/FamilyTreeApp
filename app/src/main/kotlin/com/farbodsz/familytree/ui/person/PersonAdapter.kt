package com.farbodsz.familytree.ui.person

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farbodsz.familytree.IOUtils
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.util.DATE_FORMATTER_BIRTH
import com.farbodsz.familytree.util.OnDataClick
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A [RecyclerView] adapter for displaying [people] in a standard list layout.
 */
class PersonAdapter(
        private val context: Context,
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

            val personImage = IOUtils.readPersonImage(person, context.applicationContext)
            imageView.setImageDrawable(personImage)
            imageView.borderColor = ContextCompat.getColor(context, person.gender.getColorRes())
        }
    }

    override fun getItemCount() = people.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
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
