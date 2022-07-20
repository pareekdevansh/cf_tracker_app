package com.pareekdevansh.cftracker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.models.UserResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel(private val repository: Repository) : ViewModel() {

    // search user by user handle , search contest by contest name , search a blog
    // search user
    private val _userResponseModel = MutableLiveData<Response<UserResponseModel>>()
    private val _rankColor = MutableLiveData<Int>()
    private val _maxRankColor = MutableLiveData<Int>()

    val userResponseModel : LiveData<Response<UserResponseModel>> get() = _userResponseModel
    val rankColor : LiveData<Int> get() = _rankColor
    val maxRankColor : LiveData<Int> get() = _maxRankColor

    fun searchUser(userID: String){
        viewModelScope.launch {
            val response = repository.getUser(listOf(userID))
            _userResponseModel.postValue(response)
            response.body()?.user?.get(0)?.rating?.let {
                _rankColor.postValue(updateColor(it))
            }
            response.body()?.user?.get(0)?.maxRating?.let {
                _maxRankColor.postValue(updateColor(it))
            }
        }
    }

    // updating color of the views according to rating of the user
    private fun updateColor(rating: Int): Int {
        return when (rating) {
            in 0..1199 -> R.color.rank_green
            in 2100..2399 -> R.color.rank_yellow
            in 1900..2099 -> R.color.rank_purple
            in 1600..1899 -> R.color.rank_blue
            in 1400..1599 -> R.color.rank_cyan
            in 1200..1399 -> R.color.rank_green
            else -> R.color.rank_red
        }
    }
}