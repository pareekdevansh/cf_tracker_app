package com.pareekdevansh.cftracker.ui.result.usersearchresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.Repository

class UserSearchResultViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserSearchResultViewModel(repository) as T
    }
}