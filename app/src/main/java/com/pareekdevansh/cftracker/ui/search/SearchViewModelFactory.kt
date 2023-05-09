package com.pareekdevansh.cftracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.CFRepository

class SearchViewModelFactory(private val CFRepository: CFRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(CFRepository) as T
    }
}