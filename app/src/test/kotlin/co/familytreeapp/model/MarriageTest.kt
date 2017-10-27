package co.familytreeapp.model

import org.junit.Assert.fail
import org.junit.Test
import org.threeten.bp.LocalDate

/**
 * Unit tests for [Marriage].
 */
class MarriageTest {

    @Test(expected = IllegalArgumentException::class)
    fun requirePersonCannotBeMarriedToThemselves() {
        Marriage(3, 3, LocalDate.MIN, null, "")
        fail("Expected an IllegalArgumentException to be thrown")
    }

    @Test(expected = IllegalArgumentException::class)
    fun requirePersonMustHavePositiveId() {
        Marriage(0, 2, LocalDate.MAX, null, "")
        fail("Expected an IllegalArgumentException to be thrown since person1Id < 1")

        Marriage(6, -7, LocalDate.of(2000, 3, 5), null, "")
        fail("Expected an IllegalArgumentException to be thrown since person2Id < 1")
    }

}
