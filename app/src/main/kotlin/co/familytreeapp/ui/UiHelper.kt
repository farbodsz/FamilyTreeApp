package co.familytreeapp.ui

import co.familytreeapp.ui.widget.DateViewHelper
import org.threeten.bp.LocalDate

/**
 * Object to help with displaying or customising UI components.
 */
object UiHelper {

    /**
     * Sets the default minimum/maximum dates for two person pickers for start and end dates.
     * This is based on the current person, and selections made in other person pickers.
     */
    fun setDateRangePickerConstraints(startDateHelper: DateViewHelper,
                                      endDateHelper: DateViewHelper) {
        with(startDateHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                endDateHelper.minDate = newDate
            }
        }

        with(endDateHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                startDateHelper.maxDate = newDate
            }
        }
    }

}
