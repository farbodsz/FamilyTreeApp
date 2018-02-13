package co.familytreeapp.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.model.Person
import co.familytreeapp.util.DATE_FORMATTER_BIRTH
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Type definition for an action to be preformed when a view in the [Person] list has been clicked.
 *
 * This is a function type with its parameters as the view  and [Person] that was clicked.
 * The function does not return anything.
 */
typealias OnPersonClick = (view: View, person: Person) -> Unit

/**
 * A [RecyclerView] adapter for displaying [people][Person] in a standard list layout.
 */
class PersonAdapter(
        private val context: Context,
        private val people: List<Person>
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private var onItemClickAction: OnPersonClick? = null

    fun onItemClick(action: OnPersonClick) {
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

            imageView.borderColor = ContextCompat.getColor(context, if (person.gender.isMale()) {
                R.color.image_border_male
            } else {
                R.color.image_border_female
            })
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
