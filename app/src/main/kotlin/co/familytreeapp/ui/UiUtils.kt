package co.familytreeapp.ui

import android.content.Context
import android.view.View

/**
 * Converts a dip value into pixels.
 */
fun Context.dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()
fun View.dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()

