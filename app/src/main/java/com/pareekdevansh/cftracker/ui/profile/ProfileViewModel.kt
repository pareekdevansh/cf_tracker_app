package com.pareekdevansh.cftracker.ui.profile

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pareekdevansh.cftracker.models.UserResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(private val repository: Repository) : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is profile Fragment"
//    }
//    val text: LiveData<String> = _text
    val userQuery : MutableLiveData<EditText> = MutableLiveData()
    val userResponseModel : MutableLiveData<Response<UserResponseModel>> = MutableLiveData()
    fun getUsers(){
        viewModelScope.launch {
            if(userQuery.toString().isNotEmpty()) {
                val response = repository.getUser(listOf("devanshpareek"))
//                val response = repository.getUser(listOf(userQuery.toString()))
                userResponseModel.value = response
            }
        }
    }
}