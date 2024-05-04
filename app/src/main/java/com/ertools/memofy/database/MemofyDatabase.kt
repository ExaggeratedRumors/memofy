package com.ertools.memofy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.categories.CategoryDAO
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskDAO
import com.ertools.memofy.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities=[Task::class, Category::class], version=Utils.DATABASE_VERSION)
abstract class MemofyDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDAO
    abstract fun categoryDAO(): CategoryDAO

    companion object {
        @Volatile
        private var INSTANCE : MemofyDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): MemofyDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemofyDatabase::class.java,
                    Utils.DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .addCallback(MemofyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }

        private class MemofyDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.taskDAO())
                    }
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDAO) {
            taskDao.clear()

            val task = Task(
                0,
                "Hello, World!",
                "2021-01-01",
                "2021-01-01",
                "null",
                0,
                null,
                null,
                null
            )
            taskDao.insert(task)
        }
    }
}