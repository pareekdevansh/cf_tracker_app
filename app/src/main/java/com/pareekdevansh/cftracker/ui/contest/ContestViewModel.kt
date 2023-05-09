package com.pareekdevansh.cftracker.ui.contest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.RatingChangeResponse
import com.pareekdevansh.cftracker.repository.CFRepository
import kotlinx.coroutines.*
import retrofit2.Response

class ContestViewModel(private val CFRepository: CFRepository) : ViewModel() {

    private val sevenDays = 24 * 7*  60 * 60
    val tag = "checking"
    private val _contestResponse : MutableLiveData<Response<ContestResponseModel>> = MutableLiveData()
    val contestResponse : MutableLiveData<Response<ContestResponseModel>> get() = _contestResponse
    val ratingChangeResponse : MutableLiveData<Response<RatingChangeResponse>> = MutableLiveData()

    fun getContest() {
        viewModelScope.launch {
            val response = CFRepository.getContest()
            _contestResponse.postValue(response)
        }
    }

    fun getRatingChanges(){
        viewModelScope.launch {
            val response = CFRepository.getRatingChange(1702)
            ratingChangeResponse.postValue(response)
        }
    }

}