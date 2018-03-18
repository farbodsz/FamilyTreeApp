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

import com.farbodsz.familytree.util.DATE_FORMATTER_EVENT
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

/**
 * Represents an annual recurring event related to a person, such as a birthday or anniversary.
 *
 * Events in a collection are, by default, sorted by date.
 *
 * @property dayOfMonth day of the month when the event first took place (e.g. 25)
 * @property month      [Month] of the event when the event first took place (e.g. February)
 * @property location   name of the location where the event took place
 */
sealed class Event(val dayOfMonth: Int, val month: Month, val location: String): Comparable<Event> {

    fun getDateText() = LocalDate.of(0, month, dayOfMonth).format(DATE_FORMATTER_EVENT)!!

    override fun compareTo(other: Event): Int {
        val monthComparison = month.compareTo(other.month)
        return if (monthComparison != 0) {
            monthComparison
        } else {
            // Months are equal, check dates
            dayOfMonth.compareTo(other.dayOfMonth)
        }
    }

}

class Birthday(
        val personId: Int,
        dateOfBirth: LocalDate,
        placeOfBirth: String
) : Event(dateOfBirth.dayOfMonth, dateOfBirth.month, placeOfBirth)

class Anniversary(
        val personIds: Pair<Int, Int>,
        dateOfMarriage: LocalDate,
        placeOfMarriage: String
) : Event(dateOfMarriage.dayOfMonth, dateOfMarriage.month, placeOfMarriage)

/**
 * Represents data items that have related events.
 */
interface WithEvent {

    /**
     * Returns the [Event] associated with the subclass.
     */
    fun getRelatedEvent(): Event
}
