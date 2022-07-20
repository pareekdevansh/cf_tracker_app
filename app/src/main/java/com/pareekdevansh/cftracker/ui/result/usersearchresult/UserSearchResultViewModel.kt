package com.pareekdevansh.cftracker.ui.result.usersearchresult

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.models.UserRatingResponse
import com.pareekdevansh.cftracker.models.UserResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class UserSearchResultViewModel(private val repository: Repository) : ViewModel() {

    companion object {
        const val CHAR_LEBEL = "Codeforces Rating Curve"
    }

    private val _rankColor: MutableLiveData<Int> = MutableLiveData()
    val rankColor: LiveData<Int> get() = _rankColor

    private val _maxRankColor: MutableLiveData<Int> = MutableLiveData()
    val maxRankColor: LiveData<Int> get() = _maxRankColor

    private val _userRatingResponse: MutableLiveData<Response<UserRatingResponse>> =
        MutableLiveData()
    val userRatingResponse: LiveData<Response<UserRatingResponse>> get() = _userRatingResponse

    private val _userResponseModel = MutableLiveData<Response<UserResponseModel>>()
    val userResponseModel: LiveData<Response<UserResponseModel>> get() = _userResponseModel

    private val ratingData = mutableListOf<Entry>()
    private val _lineDataSet = MutableLiveData(LineDataSet(ratingData, CHAR_LEBEL))
    val lineDataSet: LiveData<LineDataSet> get() = _lineDataSet
    var userQuery: MutableLiveData<EditText> = MutableLiveData()


    fun getUsers(userId : String) {
        viewModelScope.launch {
            if (userQuery.toString().isNotEmpty()) {
                val response = repository.getUser(listOf(userId))
//                val response = repository.getUser(listOf(userQuery.toString()))
                _rankColor.value = response.body()?.user?.get(0)?.let { updateColor(it.rating) }
                _maxRankColor.value =
                    response.body()?.user?.get(0)?.let { updateColor(it.maxRating) }
                _userResponseModel.value = response
            }
        }
    }

    fun getUserRatings( userId :String) {
        viewModelScope.launch {
            val response = repository.getUserRatings(userId)
            _userRatingResponse.postValue(response)
            response.body()?.ratingChangeList?.let {
                for (ratingChange in it) {
                    ratingData.add(
                        Entry(
                            (it.indexOf(ratingChange) + 1).toFloat(),
                            ratingChange.newRating.toFloat(),
                            ratingChange
                        )
                    )
                }
                _lineDataSet.postValue(LineDataSet((ratingData), CHAR_LEBEL))
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