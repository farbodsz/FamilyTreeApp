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

package com.farbodsz.familytree.ui.validator

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

/**
 * Responsible for validating data.
 *
 * @param T the type of data to validate (see [validate]).
 *
 * @property rootView   the view used as the root for displaying a [Snackbar]
 */
abstract class Validator<out T>(private val rootView: View) {

    /**
     * Validates the user inputs and instantiates a class of type [T] from it.
     *
     * @return  the constructed [T] object if user inputs are valid. If one or more user inputs are
     *          invalid, then this will return null.
     */
    abstract fun validate(): T?

    /**
     * Displays a [Snackbar] message using the string resource ([stringRes]) for the short duration
     * of time.
     */
    fun showMessage(@StringRes stringRes: Int) =
            Snackbar.make(rootView, stringRes, Snackbar.LENGTH_SHORT).show()

}
