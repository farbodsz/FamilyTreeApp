package com.farbodsz.familytree.ui.event

import android.content.Context
import com.farbodsz.familytree.database.manager.MarriagesManager
import com.farbodsz.familytree.database.manager.PersonManager
import com.farbodsz.familytree.model.Anniversary
import com.farbodsz.familytree.model.Birthday
import com.farbodsz.familytree.model.Event

/**
 * Convenience class for finding events from the database.
 */
class EventHandler(private val context: Context) {

    fun getEvents(): List<Event> {
        val events = ArrayList<Event>()
        events.addAll(getAnniversaries())
        events.addAll(getBirthdays())
        return events
    }

    private fun getAnniversaries(): List<Anniversary> {
        val anniversaries = ArrayList<Anniversary>()
        val marriage = MarriagesManager(context).getAll()
        for (m in marriage) {
            anniversaries.add(m.getRelatedEvent())
        }
        return anniversaries
    }

    private fun getBirthdays(): List<Birthday> {
        val birthdays = ArrayList<Birthday>()
        val people = PersonManager(context).getAll()
        for (p in people) {
            birthdays.add(p.getRelatedEvent())
        }
        return birthdays
    }

}
