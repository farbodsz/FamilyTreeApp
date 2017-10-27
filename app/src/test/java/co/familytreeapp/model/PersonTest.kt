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
            "",
            emptyList()
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

    @Test(expected = IllegalArgumentException::class)
    fun requirePersonsMarriagesMustRelateToPerson() {
        val testPersonId = 4
        val testMarriageDate = LocalDate.of(2003, 7, 2)
        val testMarriages = listOf(
                Marriage(testPersonId, 5, testMarriageDate, null, ""),
                Marriage(8, testPersonId, testMarriageDate, null, ""),
                Marriage(10, 8, testMarriageDate, null, "") // does not relate to testPersonId
        )

        testPerson.copy(marriages = testMarriages)
        fail("Expected an IllegalArgumentException to be thrown since the 3rd marriage does not " +
                "relate to testPersonId")
    }

}
