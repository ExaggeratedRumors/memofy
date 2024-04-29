package com.ertools.memofy.database

import android.provider.BaseColumns

object DatabaseContract {
    object TaskEntry: BaseColumns {
        const val TABLE_NAME = "tasks"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_FINISHED_AT = "finished_at"
        const val COLUMN_STATUS = "status"
        const val COLUMN_NOTIFICATION = "notification"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_ATTACHMENT = "attachment"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${TaskEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${TaskEntry.COLUMN_TITLE} TEXT," +
                "${TaskEntry.COLUMN_DESCRIPTION} TEXT" +
                "${TaskEntry.COLUMN_CREATED_AT} TEXT" +
                "${TaskEntry.COLUMN_FINISHED_AT} TEXT" +
                "${TaskEntry.COLUMN_STATUS} INTEGER" +
                "${TaskEntry.COLUMN_NOTIFICATION} INTEGER" +
                "${TaskEntry.COLUMN_CATEGORY} TEXT" +
                "${TaskEntry.COLUMN_ATTACHMENT} TEXT" +
                ")"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TaskEntry.TABLE_NAME}"
}