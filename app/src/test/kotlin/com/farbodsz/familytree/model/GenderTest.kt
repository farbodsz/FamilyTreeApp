/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
