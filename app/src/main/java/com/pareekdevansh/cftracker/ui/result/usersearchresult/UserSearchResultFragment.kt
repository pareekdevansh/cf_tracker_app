package com.pareekdevansh.cftracker.ui.result.usersearchresult

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.pareekdevansh.cftracker.databinding.FragmentUserSearchResultBinding
import com.pareekdevansh.cftracker.models.Submission
import com.pareekdevansh.cftracker.models.User
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.internal.artificialFrame
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserSearchResultFragment : Fragment() {

    private val args: UserSearchResultFragmentArgs by navArgs()
    private var _binding: FragmentUserSearchResultBinding? = null
    val binding get() = _binding!!
    lateinit var userSearchResultViewModel: UserSearchResultViewModel
    val repository = Repository()
    private var colorSet = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userSearchResultViewModelFactory = UserSearchResultViewModelFactory(repository)
        userSearchResultViewModel = ViewModelProvider(
            this,
            userSearchResultViewModelFactory
        )[UserSearchResultViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentUserSearchResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = args.userId
        val problemList: MutableSet<String> = mutableSetOf()
        val problemIndexes: MutableMap<String, Int> = mutableMapOf()
        val problemTags: MutableMap<String, Int> = mutableMapOf()
        val problemRatings: MutableMap<Int, Int> = mutableMapOf()
        val submissionLanguage: MutableMap<String, Int> = mutableMapOf()
        val submissionVerdict: MutableMap<String, Int> = mutableMapOf()
        generate100Colors()


        if (userId != null) {
            userSearchResultViewModel.getUsers(userId)
            userSearchResultViewModel.getUserRatings(userId)
            userSearchResultViewModel.getSubmissionsList(userId)

        }

        userSearchResultViewModel.userNotFound.observe(viewLifecycleOwner){
            if(it ){
                //TODO : user not found , show animation

            }
        }


        userSearchResultViewModel.userResponseModel.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val user = response.body()?.user?.get(0)
                if (user != null)
                    updateCurrentUser(user)
            }
        }
        userSearchResultViewModel.userRatingResponse.observe(viewLifecycleOwner)
        { response ->
            if (response.isSuccessful) {
//                Log.d(tag, response.body().toString())
                updatePerformanceCard()

            }
        }

        userSearchResultViewModel.submissionsResponse.observe(viewLifecycleOwner) { response ->
            response.body()?.let {

                CoroutineScope(Dispatchers.IO).launch {

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
                                    problemIndexes[problem.index] = 1
                                else
                                    problemIndexes[problem.index] =
                                        problemIndexes[problem.index]!! + 1


                                if (!problemRatings.containsKey(problem.rating))
                                    problemRatings[problem.rating] = 1
                                else
                                    problemRatings[problem.rating] =
                                        problemRatings[problem.rating]!! + 1

                                for (tag in problem.tags) {
                                    if (!problemTags.containsKey(tag))
                                        problemTags[tag] = 1
                                    else
                                        problemTags[tag] = problemTags[tag]!! + 1
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.loadingAnimation.apply {
                            visibility = View.GONE
                            pauseAnimation()
                        }
                        userSearchResultViewModel.lineDataSet.observe(viewLifecycleOwner) {
                            binding.ratingCurve.data = LineData(it)
                            binding.ratingCurve.invalidate()
                            makeGraphPrettier()
                        }
                        prepareRatingTable(problemRatings)
                        prepareLevelTable(problemIndexes)
                        prepareLanguageChart(submissionLanguage)
                        prepareTagsChart(problemTags)
                        prepareVerdictsChart(submissionVerdict)
                    }
                }

            }

        }

    }

    private fun generate100Colors() {
        CoroutineScope(Dispatchers.IO).launch {
            while (colorSet.size < 100) {
                val color = Color.argb(255, (1..256).random(), (1..256).random(), (1..256).random())
                if (color == Color.WHITE || color == Color.BLACK)
                    continue
                colorSet.add(color)
            }
        }
    }

    private fun updatePerformanceCard() {

        binding.apply {
            contestGiven.text = "Contests: " + userSearchResultViewModel.contestGiven.toString()
            minRatingChange.text = "Minimum Delta: " + userSearchResultViewModel.minRatingChange.toString()
            maxRatingChange.text = "Maximum Delta: " + userSearchResultViewModel.maxRatingChange.toString()
            bestRank.text = "Best Rank: " + userSearchResultViewModel.bestRank.toString()
            worstRank.text = "Worst Rank: " + userSearchResultViewModel.worstRank.toString()
        }
    }

    private fun prepareVerdictsChart(submissionVerdict: MutableMap<String, Int>) {
        val entries: MutableList<PieEntry> = mutableListOf()
        val label_entries: MutableList<String> = mutableListOf()

        for (item in submissionVerdict) {
            label_entries.add(item.key)
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }
        val pieDataSet = PieDataSet(entries, "Submission Verdicts")
        pieDataSet.apply {
            colors = colorSet
            valueTextColor = Color.BLACK;
            sliceSpace = 1f;
        }
        val pieData = PieData(pieDataSet)


        binding.pieChartVerdicts.apply {
            description.isEnabled = false
            setEntryLabelColor(Color.BLACK)
            legend.isEnabled = false
            isDrawHoleEnabled = true
            setEntryLabelTextSize(12f)
            setEntryLabelColor(R.color.black)
            centerText = "Submission Vedicts"
            setCenterTextSize(18f)

            data = pieData
            invalidate()
            animateY(2000, Easing.EaseInCirc)
        }
    }

    private fun prepareTagsChart(problemTags: MutableMap<String, Int>) {
        val entries: MutableList<PieEntry> = mutableListOf()
        for (item in problemTags) {
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }
        val pieDataSet = PieDataSet(entries, "Problem Tags")
        pieDataSet.apply {
            colors = colorSet

        }
        val pieData = PieData(pieDataSet)
        binding.pieChartTags.apply {

            description = null
            isDrawHoleEnabled = true
            setDrawEntryLabels(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(R.color.black)
            centerText = "Problem Tags"
            setCenterTextSize(20f)
            legend.isEnabled = false

            data = pieData
            invalidate()
            animateY(2000, Easing.EaseInCirc)
        }
    }

    private fun prepareLanguageChart(submissionLanguage: MutableMap<String, Int>) {
        val entries: MutableList<PieEntry> = mutableListOf()
        for (item in submissionLanguage) {
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }
        val pieDataSet = PieDataSet(entries, "Submission Languages")
        pieDataSet.apply {
            colors = colorSet

        }
        val pieData = PieData(pieDataSet)
        binding.pieChartLanguage.apply {
            description = null
            isDrawHoleEnabled = true
            setEntryLabelTextSize(12f)
            setEntryLabelColor(R.color.black)
            centerText = "Languages used"
            setCenterTextSize(20f)
            data = pieData
            legend.isEnabled = false

            invalidate()
            animateY(2000, Easing.EaseInOutElastic)
        }

    }

    private fun prepareLevelTable(problemIndexes: MutableMap<String, Int>) {
        val entries: MutableList<BarEntry> = mutableListOf()
        val xentries: MutableList<String> = mutableListOf()

        var count = 0
        for (item in problemIndexes.toSortedMap()) {
            entries.add(BarEntry(count.toFloat(), item.value.toFloat()))
            xentries.add(item.key)
            count++
        }
        val barDataSet = BarDataSet(entries, "Problem Levels")
        barDataSet.apply {
            barBorderWidth = 1f
            colors = colorSet
            valueTextColor = Color.BLACK
        }
        val barData = BarData(barDataSet)
        binding.levelTable.apply {
            axisRight.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xentries)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)
            }
            legend.form = Legend.LegendForm.NONE
            animateXY(2000, 2000)
            description = null
            data = barData
            invalidate()
        }
    }

    private fun prepareRatingTable(problemRatings: MutableMap<Int, Int>) {
        val entries: MutableList<BarEntry> = mutableListOf()
        val xentries: MutableList<String> = mutableListOf()
        var count = 0
        for (item in problemRatings.toSortedMap()) {
            if (item.key >= 800 && item.value > 0) {
                entries.add(BarEntry(count.toFloat(), item.value.toFloat()))
                xentries.add(item.key.toString())
                count++
            }
        }
        val barDataSet = BarDataSet(entries, "Problem Ratings")
        barDataSet.apply {
            barBorderWidth = 2f
            colors = colorSet
            valueTextColor = Color.BLACK
        }
        val barData = BarData(barDataSet)
        binding.ratingTable.apply {
            axisRight.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(xentries)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(true)
            }
            legend.form = Legend.LegendForm.NONE
            description = null
            animateXY(2000, 2000)
            data = barData
            invalidate()
        }
    }

    private fun makeGraphPrettier() {
        binding.ratingCurve.apply {
            axisRight.isEnabled = false
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
            updateProfileCard(user)
            updateTextFieldColor()
            // updating profile picture
            if (user.titlePhoto != null) {
                view?.let { Glide.with(it).load(user.titlePhoto).into(binding.avatar) }
            }

        }
    }

    private fun updateTextFieldColor() {
        userSearchResultViewModel.rankColor.observe(viewLifecycleOwner) { rankColor ->
            binding.apply {
                firstName.setTextColor(rankColor)
                lastName.setTextColor(rankColor)
                handle.setTextColor(rankColor)
                rating.setTextColor(rankColor)
                rank.setTextColor(rankColor)
            }
        }
        userSearchResultViewModel.maxRankColor.observe(viewLifecycleOwner) { rankColor ->
            binding.apply {
                maxRank.setTextColor(rankColor)
                maxRating.setTextColor(rankColor)
            }
        }
    }


    private fun updateProfileCard(user: User) {
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