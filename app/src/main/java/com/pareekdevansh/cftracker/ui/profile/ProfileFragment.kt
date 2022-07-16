package com.pareekdevansh.cftracker.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.pareekdevansh.cftracker.databinding.FragmentProfileBinding
import com.pareekdevansh.cftracker.models.User
import com.pareekdevansh.cftracker.repository.Repository
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "#ProfileFragment"
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var profileViewModel : ProfileViewModel
    val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModelFactory =
            ProfileViewModelFactory(repository)
        profileViewModel = ViewModelProvider(this , profileViewModelFactory)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        profileViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getUsers()
        profileViewModel.getUserRatings()

        profileViewModel.userResponseModel.observe(viewLifecycleOwner){ response ->
            if(response.isSuccessful){
                val user = response.body()?.user?.get(0)
                if(user != null )
                    updateCurrentUser(user)
            }

        }

        profileViewModel.userRatingResponse.observe(viewLifecycleOwner){ response ->
            if(response.isSuccessful){
                Log.d(tag , response.body().toString())
            }
        }

    }

    private fun updateCurrentUser(user: User) {
        binding.apply {
            updateTextFieldData(user)
            updateTextFieldColor()
            // updating profile picture
            if(user.titlePhoto != null){
                view?.let { Glide.with(it).load(user.titlePhoto).into(binding.avatar) }
            }

        }
    }

    private fun updateTextFieldColor() {
        binding.apply {
            profileViewModel.rankColor.value?.let { firstName.setTextColor(it) }
            profileViewModel.rankColor.value?.let { lastName.setTextColor(it) }
            profileViewModel.rankColor.value?.let { handle.setTextColor(it) }
            profileViewModel.maxRankColor.value?.let { maxRank.setTextColor(it) }
            profileViewModel.maxRankColor.value?.let { maxRating.setTextColor(it) }
        }
    }

    private fun updateTextFieldData(user: User) {
        binding.apply {
            contribution.text = "Contribution: " + user.contribution.toString()
            firstName.text =  user.firstName
            lastName.text =  user.lastName
            handle.text = "Handle: " +user.handle
            rating.text ="Rating: " + user.rating.toString()
            rank.text = "Rank: " +user.rank
            maxRating.text = "Max Rating: " +user.maxRating.toString()
            maxRank.text ="Max Rank: " + user.maxRank
            city.text = "City: " +user.city
            country.text = "Country: " +user.country
            organization.text ="Organization: " + user.organization
            registrationTimeSeconds.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}