package com.pareekdevansh.cftracker.ui.contest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.CFRepository

class ContestViewModelFactory(private val CFRepository: CFRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContestViewModel(CFRepository) as T
    }
}