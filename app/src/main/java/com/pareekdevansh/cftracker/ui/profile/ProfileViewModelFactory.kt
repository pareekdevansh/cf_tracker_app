package com.pareekdevansh.cftracker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pareekdevansh.cftracker.repository.CFRepository

class ProfileViewModelFactory(private val CFRepository: CFRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(CFRepository)as T
    }
}