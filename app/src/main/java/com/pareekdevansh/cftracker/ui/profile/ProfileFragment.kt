package com.pareekdevansh.cftracker.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.color.MaterialColors
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.databinding.FragmentProfileBinding
import com.pareekdevansh.cftracker.models.Submission
import com.pareekdevansh.cftracker.models.User
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var profileViewModel: ProfileViewModel
    val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModelFactory =
            ProfileViewModelFactory(repository)
        profileViewModel =
            ViewModelProvider(this, profileViewModelFactory)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var problemList: MutableSet<String> = mutableSetOf()
        var problemIndexes: MutableMap<String, Int> = mutableMapOf()
        var problemTags: MutableMap<String, Int> = mutableMapOf()
        var problemRatings: MutableMap<Int, Int> = mutableMapOf()
        var submissionLanguage: MutableMap<String, Int> = mutableMapOf()
        var submissionVerdict: MutableMap<String, Int> = mutableMapOf()


        profileViewModel.getUsers()
        profileViewModel.getUserRatings()
        profileViewModel.getSubmissionsList("devanshpareek")

        profileViewModel.userResponseModel.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val user = response.body()?.user?.get(0)
                if (user != null)
                    updateCurrentUser(user)
            }
        }
        profileViewModel.userRatingResponse.observe(viewLifecycleOwner)
        { response ->
            if (response.isSuccessful) {
//                Log.d(tag, response.body().toString())

            }
        }
        profileViewModel.lineDataSet.observe(viewLifecycleOwner) {
            binding.ratingCurve.data = LineData(it)
            binding.ratingCurve.invalidate()
            makeGraphPrettier()
        }
        profileViewModel.submissionsResponse.observe(viewLifecycleOwner) { response ->
            response.body()?.let {

                for (submission: Submission in it.submissions) {
                    if (!submissionLanguage.containsKey(submission.programmingLanguage))
                        submissionLanguage[submission.programmingLanguage] = 0
                    else
                        submissionLanguage[submission.programmingLanguage] =
                            submissionLanguage[submission.programmingLanguage]!! + 1

                    if (!submissionVerdict.containsKey(submission.verdict))
                        submissionVerdict[submission.verdict] = 0
                    else
                        submissionVerdict[submission.verdict] =
                            submissionVerdict[submission.verdict]!! + 1


                    if (submission.verdict == "OK") {
                        if (!problemList.contains(submission.problem.name)) {
                            problemList.add(submission.problem.name)
                            val problem = submission.problem
                            if (!problemIndexes.containsKey(problem.index))
                                problemIndexes[problem.index] = 0
                            else
                                problemIndexes[problem.index] = problemIndexes[problem.index]!! + 1


                            if (!problemRatings.containsKey(problem.rating))
                                problemRatings[problem.rating] = 0
                            else
                                problemRatings[problem.rating] =
                                    problemRatings[problem.rating]!! + 1

                            for (tag in problem.tags) {
                                if (!problemTags.containsKey(tag))
                                    problemTags[tag] = 0
                                else
                                    problemTags[tag] = problemTags[tag]!! + 1
                            }
                        }
                    }
                }
                prepareRatingTable(problemRatings)
                val ch = 'A'
                Log.d("oblabla", "${ch.code.toFloat().toString()}")
                prepareLevelTable(problemIndexes)

            }

        }


    }

    private fun prepareLevelTable(problemIndexes: MutableMap<String, Int>) {
        val entries: MutableList<BarEntry> = mutableListOf()
        val xentries : MutableList<String> = mutableListOf()

        var count = 0
        for (item in problemIndexes.toSortedMap()) {
            Log.d("oblabla" , count.toString())
            if(item.value != 0 ) {
                count++
                entries.add(BarEntry(count.toFloat(), item.value.toFloat()))
                xentries.add(item.key)
            }
        }
        Log.d("oblabla", entries.toString())
        val barDataSet = BarDataSet(entries, "Ratings Table")
        barDataSet.apply {
            barBorderWidth = 1f
            colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
            valueTextColor = Color.BLACK
        }
        val barData = BarData(barDataSet)
        binding.levelTable.apply {
            invalidate()
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
            data = barData
            animateXY(2000, 2000)
            description = null
        }
    }

    private fun prepareRatingTable(problemRatings: MutableMap<Int, Int>) {
        val entries: MutableList<BarEntry> = mutableListOf()
        for (item in problemRatings.toSortedMap()) {
            if(item.key >= 800 && item.value > 0 )
            entries.add(BarEntry((item.key / 100).toFloat(), item.value.toFloat()))
        }
        Log.d("oblabla", entries.toString())
        val barDataSet = BarDataSet(entries, "Problem Ratings / 100")
        barDataSet.apply {
            barBorderWidth = 2f
            colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
            valueTextColor = Color.BLACK
        }
        val barData = BarData(barDataSet)
        binding.ratingTable.apply {
            invalidate()
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
            data = barData
            animateXY(2000, 2000)
        }

    }

    private fun makeGraphPrettier() {
        binding.ratingCurve.apply {
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
            legend.isEnabled = false
            binding.ratingCurve.animateXY(2000, 2000)
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