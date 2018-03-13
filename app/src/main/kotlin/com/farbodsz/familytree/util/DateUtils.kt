package com.farbodsz.familytree.util

import org.threeten.bp.format.DateTimeFormatter

/**
 * The date pattern to use for displaying dates like 5 January 2018.
 */
@JvmField val DATE_FORMATTER_LONG: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu")

/**
 * The date pattern to use for displaying annual events (i.e. just day and month).
 */
@JvmField val DATE_FORMATTER_EVENT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
