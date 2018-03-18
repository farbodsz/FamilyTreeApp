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
