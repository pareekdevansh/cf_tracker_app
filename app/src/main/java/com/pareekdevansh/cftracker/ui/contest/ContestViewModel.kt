package com.pareekdevansh.cftracker.ui.contest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.models.Contest
import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ContestViewModel(private val repository: Repository) : ViewModel() {

    val contestResponse : MutableLiveData<Response<ContestResponseModel>> = MutableLiveData()
    fun getContest() {
        viewModelScope.launch {
            val response = repository.getContest()
            contestResponse.value = response
        }
    }

}