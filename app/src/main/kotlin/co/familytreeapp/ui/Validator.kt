package co.familytreeapp.ui

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import co.familytreeapp.R
import co.familytreeapp.model.Person
import org.threeten.bp.LocalDate

/**
 * Responsible for validating fields/properties.
 *
 * @property rootView   the view used as the root for displaying a [Snackbar]
 */
class Validator(private val rootView: View) {

    /**
     * Checks that the forename and surname are not blank, showing a message in the UI if so.
     *
     * @return true if valid
     */
    fun checkNames(forename: String, surname: String): Boolean {
        if (forename.isBlank() || surname.isBlank()) {
            Snackbar.make(
                    rootView,
                    R.string.validate_name_empty,
                    Snackbar.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    /**
     * Checks that the date of birth is not null, not in the future, and before the date of death
     * if applicable.
     *
     * @return true if valid
     */
    fun checkDates(dateOfBirth: LocalDate?, dateOfDeath: LocalDate?): Boolean {
        if (dateOfBirth == null) {
            showMessage(R.string.validate_dateOfBirth_empty)
            return false
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            showMessage(R.string.validate_dateOfBirth_future)
            return false
        }

        if (dateOfDeath != null && dateOfDeath.isBefore(dateOfBirth)) {
            showMessage(R.string.validate_dateOfDeath_beforeBirth)
            return false
        }

        return true
    }

    /**
     * Checks that there are two different people specified for the marriage.
     *
     * @return true if valid
     */
    fun checkMarriagePeople(person1: Person?, person2: Person?): Boolean {
        if (person1 == null || person2 == null) {
            showMessage(R.string.validate_marriage_people_empty)
            return false
        }

        if (person1.id == person2.id) {
            showMessage(R.string.validate_marriage_people_same)
            return false
        }

        return true
    }

    /**
     * Displays a [Snackbar] message using the string resource ([stringRes]) for the short duration
     * of time.
     */
    private fun showMessage(@StringRes stringRes: Int) =
            Snackbar.make(rootView, stringRes, Snackbar.LENGTH_SHORT).show()

}
