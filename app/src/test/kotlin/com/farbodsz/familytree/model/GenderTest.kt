package com.farbodsz.familytree.model

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

/**
 * Unit tests for [Gender].
 */
class GenderTest {

    @Test
    fun genderTypeEqualityTest() {
        val male = Gender(0)
        val female = Gender(1)

        assertEquals(male, Gender.MALE)
        assertEquals(female, Gender.FEMALE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun illegalValuesForGenderIdThrowException() {
        val illegalGender1 = Gender(2)
        fail("Expected an IllegalArgumentException to be thrown for $illegalGender1")

        val illegalGender2 = Gender(-1)
        fail("Expected an IllegalArgumentException to be thrown for $illegalGender2")
    }

}
