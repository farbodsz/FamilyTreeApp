package co.familytreeapp.ui

import android.content.Context

/**
 * Converts a dip value into pixels.
 */
fun Context.dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()
