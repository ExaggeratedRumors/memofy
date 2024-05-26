package com.ertools.memofy.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64

object BitmapConverter {
    fun bitmapToString(bitmap: Bitmap): String {
        val steam = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, steam)
        val byteArray = steam.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e:Exception) {
            e.printStackTrace()
            null
        }
    }
}