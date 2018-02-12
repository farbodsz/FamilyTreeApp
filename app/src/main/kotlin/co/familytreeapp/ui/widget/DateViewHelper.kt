package co.familytreeapp.ui.widget

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.widget.DatePicker
import co.familytreeapp.DATE_FORMATTER_BIRTH
import org.threeten.bp.LocalDate

/**
 * A helper class to display the date on a [TextInputEditText], and allowing it to be changed
 * through click then dialog.
 *
 * @param context           context from the activity/fragment
 * @param textInputEditText the [TextInputEditText] being used for the date picker
 * @param initialDate       initial [date] used for this [DateViewHelper]. It can be null (its
 *                          default value), to indicate no initial date. It can be changed later by
 *                          the [date] field.
 */
class DateViewHelper(
        private val context: Context,
        private val textInputEditText: TextInputEditText,
        initialDate: LocalDate? = null
) {

    /**
     * The [LocalDate] being displayed. This is null if no date has been selected.
     */
    var date = initialDate
        set(value) {
            field = value
            textInputEditText.setText(value?.format(DATE_FORMATTER_BIRTH))
        }

    /**
     * Function for extra actions that can be invoked after the user chooses (or changed) a date.
     */
    var onDateSet: ((view: DatePicker, date: LocalDate) -> Unit)? = null

    init {
        textInputEditText.isFocusableInTouchMode = false

        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        // N.B. month-1 and month+1 in code because Android month values are from 0-11 (to
        // correspond with java.util.Calendar) but LocalDate month values are from 1-12.

        val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
            date = newDate
            onDateSet?.invoke(view, newDate)
        }

        textInputEditText.setOnClickListener {
            val initialDate = date ?: LocalDate.now()

            DatePickerDialog(
                    context,
                    listener,
                    initialDate.year,
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
            ).show()
        }
    }

}
