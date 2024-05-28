package com.ertools.memofy.ui.tasks

import androidx.appcompat.widget.SearchView

class TasksSearchListener(
    private val tasksViewModel: TasksViewModel
) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query.isNullOrEmpty()) return false
        tasksViewModel.changeEnteredQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}