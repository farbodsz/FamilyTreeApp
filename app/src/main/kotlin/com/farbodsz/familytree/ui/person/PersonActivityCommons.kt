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

package com.farbodsz.familytree.ui.person

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Marriage
import com.farbodsz.familytree.model.Person
import com.farbodsz.familytree.ui.marriage.EditMarriageActivity

/**
 * Provides functionality common between "Person" creating/editing activities.
 */
object PersonActivityCommons {

    /**
     * Request code for starting [CreatePersonActivity] for result, to create a new [Person]
     * which would be the child of the [Person] (parent).
     */
    internal const val REQUEST_CREATE_CHILD = 4

    /**
     * Request code for starting [EditMarriageActivity] for result, to create a new [Marriage]
     */
    internal const val REQUEST_CREATE_MARRIAGE = 5

    /**
     * Request code for selecting a person image from a "gallery" app on the device.
     */
    internal const val REQUEST_PICK_IMAGE = 6

    /**
     * Represents an explicit MIME image type for use with [Intent.setType].
     */
    private const val MIME_IMAGE_TYPE = "image/*"

    /**
     * Returns an [Intent] for picking an image from the gallery app.
     */
    fun getImagePickerIntent(context: Context): Intent {
        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).setType(MIME_IMAGE_TYPE)
        val pickIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).setType(MIME_IMAGE_TYPE)

        // Chooser intent:
        return Intent.createChooser(
                getContentIntent,
                context.getString(R.string.dialog_pickImage_title)
        ).putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
    }

    /**
     * Returns a [Bitmap] received from an [Intent], or returns null and displays a [Snackbar]
     * message if it could not be received.
     *
     * @param data      the [Intent] received in [Activity.onActivityResult]
     * @param rootView  the root of the layout being displayed
     * @param contentResolver
     */
    fun getImageFromResult(data: Intent?,
                           resultCode: Int,
                           rootView: View,
                           contentResolver: ContentResolver): Bitmap? {
        return if (resultCode == Activity.RESULT_CANCELED || data == null) {
            Snackbar.make(rootView, R.string.error_couldntChangeImage, Snackbar.LENGTH_SHORT).show()
            null
        } else {
            val imageUri = data.data
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }
    }

    /**
     * Configures the input error which is displayed when the name has not been entered correctly.
     */
    fun setupNameInputError(context: Context, textInputLayout: TextInputLayout, editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isBlank()) {
                    textInputLayout.error = context.getString(R.string.error_name_empty)
                } else {
                    textInputLayout.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (editText.text.isBlank()) {
                    textInputLayout.error = context.getString(R.string.error_name_empty)
                } else {
                    textInputLayout.isErrorEnabled = false
                }
            }
        }
    }

}
