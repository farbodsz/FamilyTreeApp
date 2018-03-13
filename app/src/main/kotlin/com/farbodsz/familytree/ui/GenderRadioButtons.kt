package com.farbodsz.familytree.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.RadioButton
import com.farbodsz.familytree.model.Gender
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Encapsulates functionality relating to male and female [RadioButton]s.
 *
 * @property context            activity context
 * @property maleRadioButton    the [RadioButton] corresponding to a [Gender.MALE] selection
 * @property femaleRadioButton  the [RadioButton] corresponding to a [Gender.FEMALE] selection
 * @property circleImageView    optional reference to a [CircleImageView] so that it can be updated
 *                              with the changes to the state of the radio buttons accordingly
 */
class GenderRadioButtons(
        private val context: Context,
        private val maleRadioButton: RadioButton,
        private val femaleRadioButton: RadioButton,
        private val circleImageView: CircleImageView? = null
) {

    init {
        maleRadioButton.setOnCheckedChangeListener { _, isChecked ->
            val newGender = if (isChecked) Gender.MALE else Gender.FEMALE
            circleImageView?.borderColor = ContextCompat.getColor(context, newGender.getColorRes())
        }
        // Female radio button will respond accordingly
    }

    /**
     * Returns the [Gender] associated with the state of the male and female [RadioButton]s.
     */
    fun getGender() = if (maleRadioButton.isChecked) Gender.MALE else Gender.FEMALE

    /**
     * Checks the [RadioButton] corresponding to the [gender], and updates the linked
     * [circleImageView] if set.
     */
    fun setGender(gender: Gender) {
        maleRadioButton.isChecked = gender.isMale()
        femaleRadioButton.isChecked = gender.isFemale()
    }
}
