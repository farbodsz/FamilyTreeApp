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

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.Log
import com.farbodsz.familytree.R
import com.farbodsz.familytree.model.Person
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Provides convenience methods for I/O - reading and writing to the internal storage of the device.
 */
object IOUtils {

    private const val LOG_TAG = "IOUtils"

    @ColorRes
    private const val DEFAULT_IMAGE_COLOR_RES = R.color.grey_500

    /**
     * Saves a person's image to the internal storage.
     *
     * If an image for a person already exists, then it will be overwritten with the new [bitmap].
     *
     * @param bitmap                the [Bitmap] image to be written
     * @param personId              the ID of the [Person] this image is representing
     * @param applicationContext    the application [Context]
     */
    fun writePersonImage(bitmap: Bitmap, personId: Int, applicationContext: Context) {
        val filePath = getPersonImageFilePath(applicationContext, personId)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(filePath)
            // Use compress method on Bitmap object to write image to the OutputStream as PNG type
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream!!.close()
                Log.d(LOG_TAG, "Successfully saved image with fileName: ${filePath?.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Returns the [Drawable] image associated with the [Person] with [personId], or null if no
     * associated image could be found on the internal storage.
     */
    fun readPersonImage(personId: Int, applicationContext: Context): Drawable? {
        val filePath = getPersonImageFilePath(applicationContext, personId)
        return Drawable.createFromPath(filePath.toString())
    }

    /**
     * Returns the [Drawable] image associated with the [Person] with [personId], or a default
     * [Drawable] to use if the person's image could not be found on the internal storage.
     */
    fun readPersonImageWithDefault(personId: Int, applicationContext: Context) =
            readPersonImage(personId, applicationContext)
                    ?: getDefaultImage(applicationContext)

    fun getDefaultImage(context: Context): Drawable {
        val defaultColor = ContextCompat.getColor(context, DEFAULT_IMAGE_COLOR_RES)
        return ColorDrawable(defaultColor)
    }

    /**
     * Deletes an image on the internal storage associated with [Person] with the given [personId].
     * @return whether the deletion was successful
     */
    fun deletePersonImage(personId: Int, applicationContext: Context): Boolean {
        val filePath = getPersonImageFilePath(applicationContext, personId)
        return filePath?.delete() ?: false
    }

    /**
     * Returns the complete file path (including the file name) to the internal storage where the
     * image representing [Person] with [personId] is stored, or null if not found.
     *
     * @see getImageFilePath
     * @see getPersonImageFilename
     */
    private fun getPersonImageFilePath(applicationContext: Context, personId: Int): File? {
        val fileName = getPersonImageFilename(personId)
        return getImageFilePath(applicationContext, fileName)
    }

    /**
     * Returns the complete file path (including the file name) to the internal storage where the
     * image with given [fileName] is stored, or null if not found.
     *
     * @param applicationContext
     * @param fileName              the file name of the image, **including the file extension**.
     *
     * @see getPersonImageFilePath
     */
    private fun getImageFilePath(applicationContext: Context, fileName: String): File? {
        val contextWrapper = ContextWrapper(applicationContext)

        // Path image directory for this app (/data/data/com.farbodsz.familytree/app_data/imageDir)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)

        // Full path, including file name
        return File(directory, fileName)
    }

    /**
     * Returns the filename (including the file extension) of the image associated with [Person]
     * with given [personId].
     */
    private fun getPersonImageFilename(personId: Int) = "img_person_profile_$personId.png"

}
