package com.pareekdevansh.cftracker.ui.contest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is contest Fragment"
    }
    val text: LiveData<String> = _text
}