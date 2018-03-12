package com.farbodsz.familytree

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
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
     * Saves a [bitmap] with the given [fileName] to the internal storage.
     *
     * @param bitmap        the [Bitmap] object to be written
     * @param fileName      name of the image file. **This excludes the file extension!**
     * @param appContext    the application [Context]
     */
    fun saveBitmap(bitmap: Bitmap, fileName: String, appContext: Context) {
        val cw = ContextWrapper(appContext)
        val filePath = getImageFilePath(cw, fileName)

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
                Log.d(LOG_TAG, "Successfully saved image with fileName: $fileName")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Returns the file path of the image associated with the given [person], or null if no
     * associated image could be found on the internal storage.
     */
    fun readPersonImage(person: Person, applicationContext: Context) =
            getImageFilePath(ContextWrapper(applicationContext), getPersonImageFilename(person))

    /**
     * Returns the complete file path (including the file name) to the internal storage where the
     * image with given [fileName] is stored, or null if not found.
     *
     * @param contextWrapper
     * @param fileName          the file name of the image, **including the file extension**.
     */
    private fun getImageFilePath(contextWrapper: ContextWrapper, fileName: String): File? {
        // Path image directory for this app (/data/data/com.farbodsz.familytree/app_data/imageDir)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)

        // Full path, including file name
        return File(directory, fileName)
    }

    /**
     * Returns the filename of the image associated with the given [person].
     */
    fun getPersonImageFilename(person: Person) = "img_person_profile_${person.id}.png"

}
