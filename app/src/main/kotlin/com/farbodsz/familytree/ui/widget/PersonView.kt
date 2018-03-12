package com.farbodsz.familytree.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.farbodsz.familytree.IOUtils
import com.farbodsz.familytree.R
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.model.Person
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A compound view for displaying the icon and name of a person.
 */
class PersonView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val imageView: CircleImageView
    private val nameTextView: TextView
    private val marriageIcon: ImageView

    var person: Person? = null
        set(value) {
            if (value == null) {
                throw IllegalArgumentException("person cannot be null")
            }

            field = value

            nameTextView.text = value.fullName
            imageView.borderColor = ContextCompat.getColor(context, value.gender.getColorRes())
            imageView.setImageDrawable(
                    IOUtils.readPersonImage(value.id, context.applicationContext))

            val isMarried = MarriagesManager(context).getMarriages(value.id).isNotEmpty()
            marriageIcon.visibility = if (isMarried) View.VISIBLE else View.GONE
        }

    init {
        // Inflate XML resource for compound view with "this" as the parent
        val personView = View.inflate(context, R.layout.widget_person, this)
        imageView = personView.findViewById(R.id.circleImageView)
        nameTextView = personView.findViewById(R.id.name)
        marriageIcon = personView.findViewById(R.id.icon_married)
    }

}
