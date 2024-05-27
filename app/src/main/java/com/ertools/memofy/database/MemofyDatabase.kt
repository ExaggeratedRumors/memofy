package com.ertools.memofy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ertools.memofy.database.annexes.Annex
import com.ertools.memofy.database.annexes.AnnexDAO
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.categories.CategoryDAO
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskDAO
import com.ertools.memofy.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities=[Task::class, Category::class, Annex::class], version=Utils.DATABASE_VERSION)
abstract class MemofyDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun annexDAO(): AnnexDAO

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
                        populateDatabase(database.categoryDAO())
                    }
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDAO) {
            taskDao.clear()
            taskDao.insert(Task(
                "Hello, World!",
                "2023-01-01",
                "2024-01-01",
                "null",
                false,
                null,
                "Work"
            ))
            taskDao.insert(Task(
                "Clean room",
                "2021-01-02",
                "2021-01-03",
                "Clear room",
                completed = false,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Write CV",
                "2021-01-04",
                "2021-01-05",
                "Just send CV",
                completed = true,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Buy milk",
                "2021-01-06",
                "2021-01-07",
                "Buy milk",
                completed = false,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Buy bread",
                "2021-01-08",
                "2021-01-09",
                "Buy bread",
                completed = false,
                notification = false,
                category = "Study"
            ))
            taskDao.insert(Task(
                "Buy eggs",
                "2021-01-10",
                "2021-01-11",
                "Buy eggs",
                completed = false,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Buy butter",
                "2021-01-12",
                "2021-01-13",
                "Buy butter",
                completed= false,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Buy cheese",
                "2021-01-14",
                "2021-01-15",
                "Buy cheese",
                completed = false,
                notification = false,
                category = "Work"
            ))
            taskDao.insert(Task(
                "Buy ham",
                "2021-01-16",
                "2021-01-17",
                "Buy ham",
                completed = false,
                notification = false,
                category = "Study"
            ))
            taskDao.insert(Task(
                "Buy sausage",
                "2021-01-18",
                "2021-01-19",
                "Buy sausage",
                completed = false,
                notification = false,
                category = "Study"
            ))
            taskDao.insert(Task(
                "Buy bacon",
                "2021-01-20",
                "2021-01-21",
                "Buy bacon",
                completed = false,
                notification = false,
                category = "Work"
            ))
        }

        suspend fun populateDatabase(categoryDao: CategoryDAO) {
            categoryDao.clear()
            categoryDao.insert(Category("Work", -7077888))
            categoryDao.insert(Category("Study", -16711936))
        }
    }
}