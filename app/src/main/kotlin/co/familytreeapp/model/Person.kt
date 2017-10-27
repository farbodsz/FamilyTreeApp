package co.familytreeapp.model

import org.threeten.bp.LocalDate

/**
 * Represents a member of the family.
 *
 * @param id            a unique identifier of the person
 * @param forename      first name / forename
 * @param surname       last name / surname
 * @param gender        male or female
 * @param dateOfBirth   the date when the person was born
 * @param placeOfBirth  the place where the person was born. This is optional and can be left blank.
 * @param dateOfDeath   the date when the person died. If the person is currently alive, this should
 *                      be null.
 * @param placeOfDeath  the place where the person died. This is optional and can be left blank (for
 *                      example, if the person is currently alive).
 * @param spouseId      the integer identifier corresponding this person's spouse. If the person is
 *                      unmarried, this should be null.
 * @param dateOfMarriage the date when this person got married. If the person is unmarried, this
 *                      should be null.
 */
data class Person(
        val id: Int,
        val forename: String,
        val surname: String,
        val gender: Gender,
        val dateOfBirth: LocalDate,
        val placeOfBirth: String,
        val dateOfDeath: LocalDate?,
        val placeOfDeath: String,
        val spouseId: Int?,
        val dateOfMarriage: LocalDate?
) {

    init {
        require(id > 0) { "the id must be greater than 0" }

        dateOfDeath?.let {
            require(!it.isBefore(dateOfBirth)) {
                "the date of death cannot be before the date of birth"
            }
        }
    }

    val fullName = "$forename $surname"

    fun isAlive() = dateOfDeath == null

    fun isMarried() = dateOfMarriage == null

    override fun toString() = "$id: $fullName"

}
