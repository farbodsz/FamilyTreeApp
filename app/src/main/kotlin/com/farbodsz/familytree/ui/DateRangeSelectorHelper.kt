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

package com.farbodsz.familytree.ui

import org.threeten.bp.LocalDate

/**
 * A helper class to manage user inputs for a start date and end date.
 * The user can each of these dates through a click then dialog, and the other date
 * will update accordingly.
 *
 * @property startDateHelper    the [DateSelectorHelper] to manage inputs for the start date
 * @property endDateHelper      the [DateSelectorHelper] to manage inputs for the end date
 *
 * @see DateSelectorHelper
 */
class DateRangeSelectorHelper(
        private val startDateHelper: DateSelectorHelper,
        private val endDateHelper: DateSelectorHelper
) {

    init {
        setDateRangePickerConstraints()
    }

    fun getStartDate() = startDateHelper.date

    fun getEndDate() = endDateHelper.date

    /**
     * Sets the dates to be displayed on the date pickers, and updates the selectable date ranges of
     * each of these accordingly (i.e. their "minimum" and "maximum" dates)
     *
     * The [endDate] may be null to represent an ongoing period of time (i.e. not ended as of
     * present day).
     */
    fun setDates(startDate: LocalDate, endDate: LocalDate?) {
        with(startDateHelper) {
            date = startDate
            if (endDate != null) maxDate = endDate
        }

        with(endDateHelper) {
            date = endDate
            minDate = startDate
        }
    }

    /**
     * Sets the default minimum and maximum dates for two date pickers (for start and end dates).
     * This is based on the current person, and selections made in other date pickers.
     */
    private fun setDateRangePickerConstraints() {
        with(startDateHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                endDateHelper.minDate = newDate
            }
        }

        with(endDateHelper) {
            maxDate = LocalDate.now()
            onDateSet = { _, newDate ->
                startDateHelper.maxDate = newDate
            }
        }
    }

}
