package com.farbodsz.familytree.model

import org.threeten.bp.LocalDate
import org.threeten.bp.Month

/**
 * Represents an annual recurring event related to a person, such as a birthday or anniversary.
 *
 * @param dayOfMonth    day of the month when the event first took place (e.g. 25)
 * @param month         [Month] of the event when the event first took place (e.g. February)
 * @param location      name of the location where the event took place
 */
abstract class Event(dayOfMonth: Int, month: Month, location: String)

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
