package com.farbodsz.familytree

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
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

    /**
     * Saves a person's image to the internal storage.
     *
     * @param bitmap                the [Bitmap] image to be written
     * @param personId              the ID of the [Person] this image is representing
     * @param applicationContext    the application [Context]
     */
    fun writePersonImage(bitmap: Bitmap, personId: Int, applicationContext: Context) {
        val filePath = getImageFilePath(applicationContext, getPersonImageFilename(personId))

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
        val contextWrapper = ContextWrapper(applicationContext)
        val filePath = getImageFilePath(contextWrapper, getPersonImageFilename(personId))
        return Drawable.createFromPath(filePath.toString())
    }

    /**
     * Deletes an image on the internal storage associated with [Person] with the given [personId].
     * @return whether the deletion was successful
     */
    fun deletePersonImage(personId: Int, applicationContext: Context): Boolean {
        val filePath = getImageFilePath(applicationContext, getPersonImageFilename(personId))
        return filePath?.delete() ?: false
    }

    /**
     * Returns the complete file path (including the file name) to the internal storage where the
     * image with given [fileName] is stored, or null if not found.
     *
     * @param applicationContext
     * @param fileName          the file name of the image, **including the file extension**.
     */
    private fun getImageFilePath(applicationContext: Context, fileName: String): File? {
        val contextWrapper = ContextWrapper(applicationContext)

        // Path image directory for this app (/data/data/com.farbodsz.familytree/app_data/imageDir)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)

        // Full path, including file name
        return File(directory, fileName)
    }

    /**
     * Returns the filename of the image associated with [Person] with given [personId].
     */
    private fun getPersonImageFilename(personId: Int) = "img_person_profile_$personId.png"

}
