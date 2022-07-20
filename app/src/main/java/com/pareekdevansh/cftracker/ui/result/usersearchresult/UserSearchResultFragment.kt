package com.pareekdevansh.cftracker.ui.result.usersearchresult

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.databinding.FragmentUserSearchResultBinding
import com.pareekdevansh.cftracker.models.User
import com.pareekdevansh.cftracker.models.UserRatingResponse
import com.pareekdevansh.cftracker.models.UserResponseModel
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import retrofit2.Response

class UserSearchResultFragment : Fragment() {

    private val args: UserSearchResultFragmentArgs by navArgs()
    private var _binding: FragmentUserSearchResultBinding? = null
    val binding get() = _binding!!
    lateinit var userSearchResultViewModel: UserSearchResultViewModel
    val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userSearchResultViewModelFactory = UserSearchResultViewModelFactory(repository)
        userSearchResultViewModel = ViewModelProvider(this , userSearchResultViewModelFactory)[UserSearchResultViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentUserSearchResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.userId?.let { userSearchResultViewModel.getUsers(it) }
        args.userId?.let { userSearchResultViewModel.getUserRatings(it) }

        userSearchResultViewModel.userResponseModel.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val user = response.body()?.user?.get(0)
                if (user != null)
                    updateCurrentUser(user)
            }

        }
        userSearchResultViewModel.lineDataSet.observe(viewLifecycleOwner){
            binding.lineChart.data = LineData(it)
            binding.lineChart.invalidate()
            makeGraphPrettier()
        }

        userSearchResultViewModel.userRatingResponse.observe(viewLifecycleOwner)
        { response ->
            if (response.isSuccessful) {
                Log.d(tag, response.body().toString())

            }
        }
    }

    private fun makeGraphPrettier() {
        binding.lineChart.apply {
            axisRight.isEnabled = false
//            axisLeft.apply {
//                isEnabled =false
//            }
            xAxis.apply {
//                isEnabled = true
                isGranularityEnabled = true
                granularity = 5f
                setDrawGridLines(false)
                setDrawAxisLine(true)
                position = XAxis.XAxisPosition.BOTTOM
            }
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)
            description = null
            legend.isEnabled = true

        }
    }

    private fun updateCurrentUser(user: User) {
        binding.apply {
            updateTextFieldData(user)
            updateTextFieldColor()
            // updating profile picture
            if (user.titlePhoto != null) {
                view?.let { Glide.with(it).load(user.titlePhoto).into(binding.avatar) }
            }

        }
    }

    private fun updateTextFieldColor() {
        binding.apply {
            userSearchResultViewModel.rankColor.value?.let { firstName.setTextColor(it) }
            userSearchResultViewModel.rankColor.value?.let { lastName.setTextColor(it) }
            userSearchResultViewModel.rankColor.value?.let { handle.setTextColor(it) }
            userSearchResultViewModel.maxRankColor.value?.let { maxRank.setTextColor(it) }
            userSearchResultViewModel.maxRankColor.value?.let { maxRating.setTextColor(it) }
        }
    }

    private fun updateTextFieldData(user: User) {
        binding.apply {
            contribution.text = "Contribution: " + user.contribution.toString()
            firstName.text = user.firstName
            lastName.text = user.lastName
            handle.text = "Handle: " + user.handle
            rating.text = "Rating: " + user.rating.toString()
            rank.text = "Rank: " + user.rank
            maxRating.text = "Max Rating: " + user.maxRating.toString()
            maxRank.text = "Max Rank: " + user.maxRank
            city.text = "City: " + user.city
            country.text = "Country: " + user.country
            organization.text = "Organization: " + user.organization
            registrationTimeSeconds.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}