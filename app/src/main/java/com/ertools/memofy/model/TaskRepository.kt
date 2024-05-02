package com.ertools.memofy.model

class TaskRepository {
    val tasks = mutableListOf<TaskDTO>()
    fun modify(block: MutableList<TaskDTO>.() -> Unit) {
        synchronized(tasks) {
            block(tasks)
        }
    }

    fun fillData() {
        val tempTasks = listOf(
            TaskDTO(1, "Task 1", "2021-09-01", null, "Description 1", 0, false, "Category 1", ""),
            TaskDTO(2, "Task 2", "2021-09-02", null, "Description 2", 0, false, "Category 2", ""),
            TaskDTO(3, "Task 3", "2021-09-03", null, "Description 3", 0, false, "Category 3", ""),
            TaskDTO(4, "Task 4", "2021-09-04", null, "Description 4", 0, false, "Category 4", ""),
            TaskDTO(5, "Task 5", "2021-09-05", null, "Description 5", 0, false, "Category 5", ""),
            TaskDTO(6, "Task 6", "2021-09-06", null, "Description 6", 0, false, "Category 6", ""),
            TaskDTO(7, "Task 7", "2021-09-07", null, "Description 7", 0, false, "Category 7", ""),
            TaskDTO(8, "Task 8", "2021-09-08", null, "Description 8", 0, false, "Category 8", "")
        )
        modify { addAll(tempTasks) }
    }
}