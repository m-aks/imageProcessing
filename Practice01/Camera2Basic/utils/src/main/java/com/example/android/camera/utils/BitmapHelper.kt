package com.example.android.camera.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.BufferedInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class BitmapHelper(orientation: Int) {

    /** Default Bitmap decoding options */
    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        // Keep Bitmaps at less than 1 MP
        if (max(outHeight, outWidth) > DOWNSAMPLE_SIZE) {
            val scaleFactorX = outWidth / DOWNSAMPLE_SIZE + 1
            val scaleFactorY = outHeight / DOWNSAMPLE_SIZE + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    /** Bitmap transformation derived from passed arguments */
    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(orientation) }


    /** Utility function used to read input file into a byte array */
    fun loadInputBuffer(path: String): ByteArray {
        return BufferedInputStream(File(path).inputStream()).let { stream ->
            ByteArray(stream.available()).also {
                stream.read(it)
                stream.close()
            }
        }
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true)
    }

    companion object {
        private val TAG = BitmapHelper::class.java.simpleName

        /** Maximum size of [Bitmap] decoded */
        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP

        /**
         * Create a [File] named a using formatted timestamp with the current date and time.
         *
         * @return [File] created.
         */
        fun createFile(extension: String = "webp"): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            val path = "/storage/emulated/0/DCIM/camera2"
            return File(path, "IMG_${sdf.format(Date())}.$extension")
        }
    }
}