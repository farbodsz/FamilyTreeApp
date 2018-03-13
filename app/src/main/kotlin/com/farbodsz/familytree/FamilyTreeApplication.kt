package com.farbodsz.familytree

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * Application class for maintaining global application state.
 */
class FamilyTreeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialise DateTime library
        AndroidThreeTen.init(this)
    }

}
