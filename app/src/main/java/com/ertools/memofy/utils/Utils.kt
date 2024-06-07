package com.ertools.memofy.utils

object Utils {
    /** Database **/
    const val DATABASE_NAME = "memofy.db"
    const val DATABASE_VERSION = 7
    const val DATABASE_TASK_TABLE = "tasks"

    /** Externals **/
    const val ATTACHMENTS_DIRECTORY = "MemofyAttachments"

    /** Intent **/
    const val INTENT_TASK_TITLE = "task_title"
    const val INTENT_TASK_DESCRIPTION = "task_description"
    const val INTENT_TASK_ID = "task_id"

    /** Bundle **/
    const val BUNDLE_STATUS_FILTER = "status_filter"
    const val BUNDLE_TASK = "task"

    /** Notifications **/
    const val NOTIFICATION_CHANNEL_TASK = "task_channel"
    const val NOTIFICATION_TIME_DEFAULT = 1

    /** Shared preferences **/
    const val SHARED_PREFERENCES_NAME = "memofy_preferences"
    const val SHARED_PREFERENCES_DELAY = "sp_delay"

    /** Date **/
    const val DATE_FORMAT = "yyyy-MM-dd HH:mm"
}