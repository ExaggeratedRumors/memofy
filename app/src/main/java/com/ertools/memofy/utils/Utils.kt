package com.ertools.memofy.utils

object Utils {
    /** Database **/
    const val DATABASE_NAME = "memofy.db"
    const val DATABASE_VERSION = 7

    /** Codes **/
    const val PICK_FILE_REQUEST_CODE = 100

    /** Externals **/
    const val ATTACHMENTS_DIRECTORY = "MemofyAttachments"

    /** Intent **/
    const val INTENT_TASK_TITLE = "task_title"
    const val INTENT_TASK_DESCRIPTION = "task_description"

    /** Bundle **/
    const val BUNDLE_STATUS_FILTER = "status_filter"

    /** Notifications **/
    const val NOTIFICATION_CHANNEL_TASK = "task_channel"
    const val NOTIFICATION_DATA_TITLE = "title"
    const val NOTIFICATION_DATA_DESCRIPTION = "description"
    const val NOTIFICATION_DATA_FINISH_TIME = "finishedAt"
    const val NOTIFICATION_DATA_FINISH_DELAY = "minutesBefore"
    const val NOTIFICATION_TIME_DEFAULT = 5
}