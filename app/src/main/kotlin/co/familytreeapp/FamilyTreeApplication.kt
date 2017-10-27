package co.familytreeapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class FamilyTreeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
    }

}
