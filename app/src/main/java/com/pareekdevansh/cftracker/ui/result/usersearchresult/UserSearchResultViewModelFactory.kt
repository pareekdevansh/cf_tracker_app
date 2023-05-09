package com.pareekdevansh.cftracker.ui.result.usersearchresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.CFRepository

class UserSearchResultViewModelFactory(private val CFRepository: CFRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserSearchResultViewModel(CFRepository) as T
    }
}