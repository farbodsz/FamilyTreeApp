package co.familytreeapp.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.database.manager.PersonManager
import co.familytreeapp.model.Marriage
import co.familytreeapp.util.DATE_FORMATTER_BIRTH
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Type definition for an action to be preformed when a view in the [Marriage] list has been clicked.
 *
 * This is a function type with its parameters as the view  and [Marriage] that was clicked.
 * The function does not return anything.
 */
typealias OnMarriageClick = (view: View, marriage: Marriage) -> Unit

/**
 * A [RecyclerView] adapter for displaying [marriages][Marriage] in a standard list layout.
 */
class MarriageAdapter(
        private val context: Context,
        private val personId: Int,
        private val marriages: List<Marriage>
) : RecyclerView.Adapter<MarriageAdapter.ViewHolder>() {

    private var onItemClickAction: OnMarriageClick? = null

    fun onItemClick(action: OnMarriageClick) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_person, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val marriage = marriages[position]
        val spouseId =
                if (marriage.person1Id == personId) marriage.person2Id else marriage.person1Id
        val spouse = PersonManager(context).get(spouseId)

        with(holder!!) {
            nameText.text = spouse.fullName
            infoText.text = marriage.startDate.format(DATE_FORMATTER_BIRTH) // TODO show end date too

            imageView.borderColor = ContextCompat.getColor(context, if (spouse.gender.isMale()) {
                R.color.image_border_male
            } else {
                R.color.image_border_female
            })
        }
    }

    override fun getItemCount() = marriages.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
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
