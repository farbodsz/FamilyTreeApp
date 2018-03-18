/*
 * Copyright 2018 Farbod Salamat-Zadeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
