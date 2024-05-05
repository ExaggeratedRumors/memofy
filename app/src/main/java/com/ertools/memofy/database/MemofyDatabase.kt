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
            taskDao.insert(Task(
                0,
                "Hello, World!",
                "2023-01-01",
                "2024-01-01",
                "null",
                0,
                null,
                3,
                null
            ))
            taskDao.insert(Task(
                1,
                "Clean room",
                "2021-01-02",
                "2021-01-03",
                "Clear room",
                0,
                false,
                1,
                null
            ))
            taskDao.insert(Task(
                2,
                "Write CV",
                "2021-01-04",
                "2021-01-05",
                "Just send CV",
                1,
                false,
                2,
                null
            ))
            taskDao.insert(Task(
                3,
                "Buy milk",
                "2021-01-06",
                "2021-01-07",
                "Buy milk",
                0,
                false,
                3,
                null
            ))
            taskDao.insert(Task(
                4,
                "Buy bread",
                "2021-01-08",
                "2021-01-09",
                "Buy bread",
                0,
                false,
                4,
                null
            ))
            taskDao.insert(Task(
                5,
                "Buy eggs",
                "2021-01-10",
                "2021-01-11",
                "Buy eggs",
                0,
                false,
                5,
                null
            ))
            taskDao.insert(Task(
                6,
                "Buy butter",
                "2021-01-12",
                "2021-01-13",
                "Buy butter",
                0,
                false,
                6,
                null
            ))
            taskDao.insert(Task(
                7,
                "Buy cheese",
                "2021-01-14",
                "2021-01-15",
                "Buy cheese",
                0,
                false,
                7,
                null
            ))
            taskDao.insert(Task(
                8,
                "Buy ham",
                "2021-01-16",
                "2021-01-17",
                "Buy ham",
                0,
                false,
                8,
                null
            ))
            taskDao.insert(Task(
                9,
                "Buy sausage",
                "2021-01-18",
                "2021-01-19",
                "Buy sausage",
                0,
                false,
                9,
                null
            ))
            taskDao.insert(Task(
                10,
                "Buy bacon",
                "2021-01-20",
                "2021-01-21",
                "Buy bacon",
                0,
                false,
                10,
                null
            ))
        }
    }
}