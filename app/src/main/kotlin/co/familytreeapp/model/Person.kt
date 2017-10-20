package co.familytreeapp.model

/**
 * Represents a member of the family.
 *
 * @param id        a unique identifier of the person
 * @param forename  first name / forename
 * @param surname   last name / surname
 * @param gender    male or female
 */
data class Person(
        val id: Int,
        val forename: String,
        val surname: String,
        val gender: Gender
) {

    val fullName = "$forename $surname"

    override fun toString() = "$id: $fullName"

}
