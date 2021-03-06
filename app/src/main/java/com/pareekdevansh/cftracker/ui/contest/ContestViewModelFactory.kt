package com.pareekdevansh.cftracker.ui.contest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.Repository

class ContestViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContestViewModel(repository) as T
    }
}