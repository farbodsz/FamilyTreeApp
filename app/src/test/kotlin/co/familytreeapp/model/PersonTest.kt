package co.familytreeapp.model

import org.junit.Assert.fail
import org.junit.Test
import org.threeten.bp.LocalDate

/**
 * Unit tests for [Person].
 */
class PersonTest {

    /**
     * A dummy [Person] object to help with writing tests, because [Person] has a lot of properties
     * that would otherwise be very verbose to specify in each test.
     */
    private val testPerson = Person(
            4,
            "Farbod",
            "Salamat-Zadeh",
            Gender.MALE,
            LocalDate.of(2000, 5, 7),
            "Oxford",
            null,
            ""
    )

    @Test(expected = IllegalArgumentException::class)
    fun requirePersonMustHavePositiveId() {
        testPerson.copy(id = -4)
        fail("Expected an IllegalArgumentException to be thrown since id < 1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun requirePersonMustHaveName() {
        testPerson.copy(forename = "")
        fail("Expected an IllegalArgumentException to be thrown since forename is blank")

        testPerson.copy(surname = "   ")
        fail("Expected an IllegalArgumentException to be thrown since surname is blank")
    }

    @Test(expected = IllegalArgumentException::class)
    fun requireDateOfDeathCannotBeBeforeDateOfBirth() {
        testPerson.copy(
                dateOfBirth = LocalDate.of(1998, 7, 3),
                dateOfDeath = LocalDate.of(1997, 1, 30)
        )
        fail("Expected an IllegalArgumentException to be thrown since date of death is before " +
                "date of birth")
    }

}
