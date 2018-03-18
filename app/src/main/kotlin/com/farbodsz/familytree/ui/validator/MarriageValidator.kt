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

package com.farbodsz.familytree.ui.validator

import android.view.View
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Marriage
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.util.toTitleCase
import org.threeten.bp.LocalDate

/**
 * Responsible for validating a [Marriage].
 *
 * @see Marriage
 * @see Validator
 */
class MarriageValidator(
        rootView: View,
        private val person1: Person?,
        private val person2: Person?,
        private val startDate: LocalDate?,
        private val endDate: LocalDate?,
        private val placeText: String
) : Validator<Marriage>(rootView) {

    override fun validate(): Marriage? {
        if (!checkPeople(person1, person2)) return null

        // Dates should be ok from dialog constraint, but best to double-check before db write
        if (!checkDates(startDate, endDate)) return null

        return Marriage(
                person1!!.id,
                person2!!.id,
                startDate!!,
                endDate,
                placeText.trim().toTitleCase()
        )
    }

    /**
     * Checks that there are two different people specified for the marriage.
     *
     * @return true if valid
     */
    private fun checkPeople(person1: Person?, person2: Person?): Boolean {
        if (person1 == null || person2 == null) {
            showMessage(R.string.validate_marriage_people_empty)
            return false
        }

        if (person1.id == person2.id) {
            showMessage(R.string.validate_marriage_people_same)
            return false
        }

        return true
    }

    /**
     * Checks that the start date is not null, not in the future, and is before the end date if
     * applicable.
     *
     * @return true if valid
     */
    private fun checkDates(dateOfBirth: LocalDate?, dateOfDeath: LocalDate?): Boolean {
        if (dateOfBirth == null) {
            showMessage(R.string.validate_startDate_empty)
            return false
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            showMessage(R.string.validate_startDate_future)
            return false
        }

        if (dateOfDeath != null && dateOfDeath.isBefore(dateOfBirth)) {
            showMessage(R.string.validate_endDate_beforeStart)
            return false
        }

        return true
    }

}
