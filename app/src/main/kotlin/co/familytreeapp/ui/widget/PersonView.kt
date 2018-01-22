package co.familytreeapp.ui.widget

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import co.familytreeapp.R
import co.familytreeapp.model.Person
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

    private var name = ""
        set(value) {
            field = value
            nameTextView.text = name
        }

    @ColorRes private var iconBorderColorRes = R.color.black
        set(value) {
            field = value
            imageView.borderColor = ContextCompat.getColor(context, iconBorderColorRes)
        }

    // TODO setting image resource

    var person: Person? = null
        set(value) {
            if (value == null) {
                throw IllegalArgumentException("person cannot be null")
            }

            field = value

            name = value.fullName
            iconBorderColorRes = if (value.gender.isMale()) {
                R.color.image_border_male
            } else {
                R.color.image_border_female
            }
        }

    init {
        // Inflate XML resource for compound view with "this" as the parent
        val personView = View.inflate(context, R.layout.widget_person, this)
        imageView = personView.findViewById(R.id.circleImageView)
        nameTextView = personView.findViewById(R.id.name)
    }

}
