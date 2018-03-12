package com.farbodsz.familytree.ui.event

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Anniversary
import com.farbodsz.familytree.model.Birthday
import com.farbodsz.familytree.model.Event
import com.farbodsz.familytree.util.IOUtils
import com.farbodsz.familytree.util.OnDataClick
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A [RecyclerView] adapter for displaying [events] in a standard list layout.
 */
class EventAdapter(
        private val context: Context,
        private val events: List<Event>
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private var onItemClickAction: OnDataClick<Event>? = null

    fun onItemClick(action: OnDataClick<Event>) {
        onItemClickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_list_event, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val event = events[position]
        when (event) {
            is Anniversary -> setupAnniversaryLayout(holder!!, event)
            is Birthday -> setupBirthdayLayout(holder!!, event)
        }
    }

    private fun setupAnniversaryLayout(holder: ViewHolder, anniversary: Anniversary) {
        val marriage = MarriagesManager(context).get(anniversary.personIds)

        val pm = PersonManager(context)
        val person1 = pm.get(marriage.person1Id)
        val person2 = pm.get(marriage.person2Id)

        with(holder) {
            imageIcon.setImageResource(R.drawable.ic_marriage_black_24dp)
            titleText.text = person1.fullName + " and " + person2.fullName
            subtitleText.text = anniversary.getDateText()

            val imageDrawable1 = IOUtils.readPersonImage(person1.id, context.applicationContext)
            person1Image.setImageDrawable(imageDrawable1)
            person1Image.borderColor = ContextCompat.getColor(context, person1.gender.getColorRes())

            val imageDrawable2 = IOUtils.readPersonImage(person2.id, context.applicationContext)
            person2Image.setImageDrawable(imageDrawable2)
            person2Image.borderColor = ContextCompat.getColor(context, person2.gender.getColorRes())
        }
    }

    private fun setupBirthdayLayout(holder: ViewHolder, birthday: Birthday) {
        val person = PersonManager(context).get(birthday.personId)

        with(holder) {
            imageIcon.setImageResource(R.drawable.ic_birthday_black_24dp)
            titleText.text = person.fullName
            subtitleText.text = birthday.getDateText()

            val imageDrawable = IOUtils.readPersonImage(person.id, context.applicationContext)
            person1Image.setImageDrawable(imageDrawable)
            person1Image.borderColor = ContextCompat.getColor(context, person.gender.getColorRes())
            person2Image.visibility = View.GONE
        }
    }

    override fun getItemCount() = events.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageIcon: ImageView = itemView.findViewById(R.id.imageView_icon)
        val titleText: TextView = itemView.findViewById(R.id.title)
        val subtitleText: TextView = itemView.findViewById(R.id.subtitle)
        val person1Image: CircleImageView = itemView.findViewById(R.id.imageView_person1)
        val person2Image: CircleImageView = itemView.findViewById(R.id.imageView_person2)

        init {
            itemView.setOnClickListener {
                val position = layoutPosition
                onItemClickAction?.invoke(it, events[position])
            }
        }

    }

}
