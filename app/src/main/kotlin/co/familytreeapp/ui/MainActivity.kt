package co.familytreeapp.ui

import android.os.Bundle
import co.familytreeapp.R
import co.familytreeapp.util.standardNavigationParams
import co.familytreeapp.util.withNavigation

/**
 * The main screen of the app.
 */
class MainActivity : NavigationDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(withNavigation(R.layout.activity_main))
    }

    override fun getSelfNavigationParams() =
            standardNavigationParams(NAVDRAWER_ITEM_MAIN, findViewById(R.id.toolbar))

}
