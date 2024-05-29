package com.ertools.memofy.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.ertools.memofy.utils.Utils

class SettingsViewModel: ViewModel() {
    fun saveDelayToPreferences(context: Context, value: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(Utils.SHARED_PREFERENCES_DELAY, value)
        editor.apply()
    }

    fun getDelayFromPreferences(context: Context): Int {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(Utils.SHARED_PREFERENCES_DELAY, -1)
    }
}