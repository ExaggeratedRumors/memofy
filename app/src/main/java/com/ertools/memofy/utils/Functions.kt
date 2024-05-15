package com.ertools.memofy.utils

import android.os.Build
import android.os.Bundle
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

fun timestampToTime(timestamp: Long): String {
    val timeInMs = timestamp * 1000
    val date = java.util.Date(timeInMs)
    val sdf = SimpleDateFormat("EEEE dd/MM/yyyy HH:mm", Locale.ENGLISH)
    return sdf.format(date)
}