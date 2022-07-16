package com.pareekdevansh.cftracker.ui.profile

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.models.UserResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    private var _rankColor: MutableLiveData<Int> = MutableLiveData()
    private var _maxRankColor: MutableLiveData<Int> = MutableLiveData()
    val rankColor get() = _rankColor
    val maxRankColor get() = _maxRankColor
    var userQuery: MutableLiveData<EditText> = MutableLiveData()
    val userResponseModel: MutableLiveData<Response<UserResponseModel>> = MutableLiveData()
    fun getUsers() {
        viewModelScope.launch {
            if (userQuery.toString().isNotEmpty()) {
                val response = repository.getUser(listOf("devanshpareek"))
//                val response = repository.getUser(listOf(userQuery.toString()))
                _rankColor.value = response.body()?.user?.get(0)?.let { updateColor(it.rating) }
                _maxRankColor.value =
                    response.body()?.user?.get(0)?.let { updateColor(it.maxRating) }
                userResponseModel.value = response
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