package co.familytreeapp.ui.widget

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.widget.DatePicker
import co.familytreeapp.util.DATE_FORMATTER_BIRTH
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset

/**
 * A helper class to display the date on a [TextInputEditText], and allowing it to be changed
 * through click then dialog.
 *
 * @param context           context from the activity/fragment
 * @param textInputEditText the [TextInputEditText] being used for the date picker
 * @param initialDate       initial [date] used for this [DateSelectorHelper]. It can be null (its
 *                          default value), to indicate no initial date. It can be changed later by
 *                          the [date] field.
 */
class DateSelectorHelper(
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

    /**
     * The minimum date available in the [DatePicker].
     *
     * It can be null (its default value) to indicate no minimum date. It must be less than the
     * maximum date, if specified.
     *
     * @see maxDate
     */
    var minDate: LocalDate? = null
        set(value) {
            requireDateBoundaries(value, maxDate)
            field = value
        }

    /**
     * The maximum date available in the [DatePicker].
     *
     * It can be null (its default value) to indicate no maximum date. It must be greater than the
     * minimum date, if specified.
     *
     * @see minDate
     */
    var maxDate: LocalDate? = null
        set(value) {
            requireDateBoundaries(minDate, value)
            field = value
        }

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

            val datePickerDialog = DatePickerDialog(
                    context,
                    listener,
                    initialDate.year,
                    initialDate.monthValue - 1,
                    initialDate.dayOfMonth
            )

            minDate?.let { datePickerDialog.datePicker.minDate = dateToMs(it) }
            maxDate?.let { datePickerDialog.datePicker.maxDate = dateToMs(it) }

            datePickerDialog.show()
        }
    }

    /**
     * Converts a date to the number of milliseconds since 01/01/1970.
     * (The Android API requires minimum/maximum date in milliseconds).
     */
    private fun dateToMs(date: LocalDate) =
            date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    /**
     * Checks if the minimum date is before the maximum date, throwing an [IllegalArgumentException]
     * if not.
     *
     * @see minDate
     * @see maxDate
     */
    private fun requireDateBoundaries(min: LocalDate?, max: LocalDate?) {
        if (min != null && max != null) {
            require(min.isBefore(max)) {
                "the minimum date must be less than the maximum date when both are specified"
            }
        }
    }

}
