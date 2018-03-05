package com.farbodsz.familytree.util

import org.threeten.bp.format.DateTimeFormatter

/**
 * The date pattern to use for displaying dates of birth/death on the edit page.
 */
@JvmField val DATE_FORMATTER_BIRTH: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu")
