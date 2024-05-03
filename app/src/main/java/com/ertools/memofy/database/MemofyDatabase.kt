package com.ertools.memofy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ertools.memofy.utils.Utils

@Database(entities=[Task::class, Category::class], version=1)
abstract class MemofyDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDAO
    abstract fun categoryDAO(): CategoryDAO

    companion object {
        var instance : MemofyDatabase? = null

        fun getInstance(context: Context): MemofyDatabase {
            if(instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemofyDatabase::class.java,
                    Utils.DATABASE_NAME
                ).build()
            }
            return instance!!
        }
    }
}