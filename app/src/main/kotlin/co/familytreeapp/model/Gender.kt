package co.familytreeapp.model

/**
 * Represents a gender.
 *
 * @param id    an integer identifier corresponding to a gender. 0 = male; 1 = female.
 */
data class Gender(val id: Int) {

    init {
        if (id !in 0..1) {
            throw IllegalArgumentException("the id for Gender must be between 0 and 1")
        }
    }

    companion object {
        @JvmField val MALE = Gender(0)
        @JvmField val FEMALE = Gender(1)
    }

    fun isMale() = this == MALE

    fun isFemale() = this == FEMALE

}
