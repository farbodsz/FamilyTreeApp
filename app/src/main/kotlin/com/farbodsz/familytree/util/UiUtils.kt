package com.farbodsz.familytree.util

import android.app.Activity
import android.content.Context
import android.support.annotation.LayoutRes
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.NavigationParameters

/**
 * Type definition for an action to be preformed when a view in the list has been clicked.
 *
 * This is a function type with its parameters as the view that was clicked and the
 * [layout position][RecyclerView.ViewHolder.getLayoutPosition] of the ViewHolder. The function
 * returns [Unit].
 */
typealias OnItemClick = (view: View, position: Int) -> Unit

/**
 * Type definition for an action to be preformed when a [Person] in a collection of items has been
 * clicked.
 *
 * This is a function type with two parameters: the [View] that was clicked/selected, and the
 * corresponding [Person] from the collection. The function returns [Unit].
 */
typealias OnPersonClick = (view: View, person: Person) -> Unit

/**
 * Converts a dip value into pixels.
 */
fun Context.dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()
fun View.dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()

/**
 * Returns a layout with an added navigation drawer (using the template `activity_navigation.xml`).
 *
 * @param layoutRes the layout resource of the activity
 * @return the inflated layout resource inside the content frame of the navigation layout
 */
fun Context.withNavigation(@LayoutRes layoutRes: Int): View {
    val layoutInflater = LayoutInflater.from(this)

    val navigationLayout = layoutInflater.inflate(R.layout.activity_navigation, null)
    val activityLayout = layoutInflater.inflate(layoutRes, null)

    navigationLayout.findViewById<FrameLayout>(R.id.content_frame).apply {
        removeAllViews()
        addView(activityLayout)
    }

    return navigationLayout
}

/**
 * Helper function for returning a [NavigationParameters] using values from the default navigation
 * layout (from template `activity_navigation.xml`).
 *
 * @param navigationItem    an integer representing the navigation item in the menu
 * @param toolbar           the [Toolbar] being used in the layout of the activity (subclass of
 *                          [NavigationDrawerActivity]). Null if no [Toolbar] is being used.
 *
 * @return  the [NavigationParameters] from the values provided; or null if the [toolbar],
 *          [DrawerLayout], or [NavigationView] is null.
 *
 * @see NavigationParameters
 */
fun Activity.standardNavigationParams(navigationItem: Int, toolbar: Toolbar?): NavigationParameters? {
    val drawerLayout = findViewById<DrawerLayout?>(R.id.drawerLayout)
    val navigationView = findViewById<NavigationView?>(R.id.navigationView)

    return if (toolbar == null || drawerLayout == null || navigationView == null) {
        null
    } else {
        NavigationParameters(navigationItem, drawerLayout, navigationView, toolbar)
    }
}
