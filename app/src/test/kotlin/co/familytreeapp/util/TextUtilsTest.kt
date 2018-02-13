package co.familytreeapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for util functions in [co.familytreeapp.util.TextUtilsTest].
 */
class TextUtilsTest {

    @Test
    fun toTitleCaseTest() {
        val string1 = "test"
        assertEquals("Test", string1.toTitleCase())

        val string2 = "seCONd TeST"
        assertEquals("Second Test", string2.toTitleCase())

        val string3 = "hyphen-test"
        assertEquals("Hyphen-Test", string3.toTitleCase('-'))

        val string4 = "(other/delimiter*test)"
        assertEquals("(Other/Delimiter*Test)", string4.toTitleCase('(', ')', '/', '*'))
    }

}
