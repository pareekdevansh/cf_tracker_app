package com.pareekdevansh.cftracker.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.databinding.FragmentProfileBinding
import com.pareekdevansh.cftracker.models.Submission
import com.pareekdevansh.cftracker.models.User
import com.pareekdevansh.cftracker.repository.CFRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var profileViewModel: ProfileViewModel
    val CFRepository = CFRepository()
    private var colorSet = mutableListOf<Int>()
    private var moreInfoCardExpanded = true
    private var performanceCardExpanded = true
    private var ratingCurveExpanded = true
    private var ratingTableExpanded = true
    private var levelTableExpanded = true
    private var problemTagsChartExpanded = true
    private var languageTagsChartExpanded = true
    private var submissionVerdictsExpanded = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModelFactory =
            ProfileViewModelFactory(CFRepository)
        profileViewModel =
            ViewModelProvider(this, profileViewModelFactory)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val problemList: MutableSet<String> = mutableSetOf()
        val problemIndexes: MutableMap<String, Int> = mutableMapOf()
        val problemTags: MutableMap<String, Int> = mutableMapOf()
        val problemRatings: MutableMap<Int, Int> = mutableMapOf()
        val submissionLanguage: MutableMap<String, Int> = mutableMapOf()
        val submissionVerdict: MutableMap<String, Int> = mutableMapOf()

        generate100Colors()


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
                updatePerformanceCard()

            }
        }

        profileViewModel.submissionsResponse.observe(viewLifecycleOwner) { response ->
            response.body()?.let {

                CoroutineScope(Dispatchers.IO).launch {

                    for (submission: Submission in it.submissions) {
                        if (!submissionLanguage.containsKey(submission.programmingLanguage))
                            submissionLanguage[submission.programmingLanguage] = 1
                        else
                            submissionLanguage[submission.programmingLanguage] =
                                submissionLanguage[submission.programmingLanguage]!! + 1

                        if (!submissionVerdict.containsKey(submission.verdict))
                            submissionVerdict[submission.verdict] = 1
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
                        binding.completeScreen.visibility = View.VISIBLE
                        profileViewModel.lineDataSet.observe(viewLifecycleOwner) {
                            binding.ratingCurve.data = LineData(it)
                            binding.ratingCurve.invalidate()
                            makeGraphPrettier()
                        }
                        prepareRatingTable(problemRatings)
                        prepareLevelTable(problemIndexes)
                        prepareLanguageChart(submissionLanguage)
                        prepareTagsChart(problemTags)
                        prepareSubmissionVerdictsChart(submissionVerdict)
                    }
                }

            }

        }

        binding.btnMoreInfoCardExpand.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (moreInfoCardExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                moreInfoDropDownCL.visibility = viewVisibility
                moreInfoDropDownCL.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        anim
                    )
                )
                btnMoreInfoCardExpand.setImageResource(imageId)
            }
            moreInfoCardExpanded = !moreInfoCardExpanded

        }

        binding.btnExpandPerformanceCard.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (performanceCardExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                performanceDropDownCL.visibility = viewVisibility
                performanceDropDownCL.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        anim
                    )
                )
                btnExpandPerformanceCard.setImageResource(imageId)
            }
            performanceCardExpanded = !performanceCardExpanded
        }

        binding.btnExpandRatingCurve.setOnClickListener {
            val viewVisibility: Int
            val imageId: Int
            val anim: Int
            if (ratingCurveExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                ratingCurve.visibility = viewVisibility
                ratingCurve.startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
                btnExpandRatingCurve.setImageResource(imageId)
            }
            ratingCurveExpanded = !ratingCurveExpanded
        }

        binding.btnExpandRatingTable.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (ratingTableExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                ratingTable.visibility = viewVisibility
                ratingTable.startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
                btnExpandRatingTable.setImageResource(imageId)
            }
            ratingTableExpanded = !ratingTableExpanded
        }

        binding.btnExpandLevelTable.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (levelTableExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                levelTable.visibility = viewVisibility
                levelTable.startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
                btnExpandLevelTable.setImageResource(imageId)
            }
            levelTableExpanded = !levelTableExpanded
        }

        binding.btnExpandProblemTagsChart.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (problemTagsChartExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                pieChartProblemTags.visibility = viewVisibility
                pieChartProblemTags.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        anim
                    )
                )
                btnExpandProblemTagsChart.setImageResource(imageId)
            }
            problemTagsChartExpanded = !problemTagsChartExpanded
        }

        binding.btnExpandLanguageTagsChart.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (languageTagsChartExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                pieChartLanguage.visibility = viewVisibility
                pieChartLanguage.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        anim
                    )
                )
                btnExpandLanguageTagsChart.setImageResource(imageId)
            }
            languageTagsChartExpanded = !languageTagsChartExpanded
        }

        binding.btnExpandSubmissionVerdicts.setOnClickListener {
            var viewVisibility: Int
            var imageId: Int
            var anim: Int
            if (submissionVerdictsExpanded) {
                anim = R.anim.slide_up_anim
                viewVisibility = View.GONE
                imageId = R.drawable.baseline_expand_more_24
            } else {
                anim = R.anim.slide_down_anim
                imageId = R.drawable.baseline_expand_less_24
                viewVisibility = View.VISIBLE
            }
            binding.apply {
                pieChartVerdicts.visibility = viewVisibility
                pieChartVerdicts.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        anim
                    )
                )
                btnExpandSubmissionVerdicts.setImageResource(imageId)
            }
            submissionVerdictsExpanded = !submissionVerdictsExpanded
        }


    }

    private fun generate100Colors() {
        CoroutineScope(Dispatchers.IO).launch {
            while (colorSet.size < 100) {
                val color =
                    Color.argb(255, (100..256).random(), (100..256).random(), (100..256).random())
                if (color == Color.WHITE || color == Color.BLACK)
                    continue
                colorSet.add(color)
            }
        }
    }

    private fun updatePerformanceCard() {

        binding.apply {
            contestGiven.text = "Contests: " + profileViewModel.contestGiven.toString()
            minRatingChange.text = "Minimum Delta: " + profileViewModel.minRatingChange.toString()
            maxRatingChange.text = "Maximum Delta: " + profileViewModel.maxRatingChange.toString()
            bestRank.text = "Best Rank: " + profileViewModel.bestRank.toString()
            worstRank.text = "Worst Rank: " + profileViewModel.worstRank.toString()
        }
    }

    private fun prepareSubmissionVerdictsChart(submissionVerdict: MutableMap<String, Int>) {
        val entries: MutableList<PieEntry> = mutableListOf()
        val label_entries: MutableList<String> = mutableListOf()

        for (item in submissionVerdict) {
            label_entries.add(item.key)
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }
        val pieDataSet = PieDataSet(entries, "Submission Verdicts")
        pieDataSet.apply {
            colors = colorSet
            valueTextSize = 14f
            valueTextColor = Color.BLACK;
            sliceSpace = 1f;
            xValuePosition = (PieDataSet.ValuePosition.OUTSIDE_SLICE)
            valueLinePart1OffsetPercentage = 10f
            valueLinePart1Length = 0.1f

            valueLinePart2Length = 0.4f

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
            dragDecelerationFrictionCoef = .9f
            isHighlightPerTapEnabled = true
            rotationAngle = 0f
            setCenterTextSize(18f)
            data = pieData

            invalidate()

            animateY(2000, Easing.EaseInCirc)
            setOnChartValueSelectedListener(PieChartOnChartValueSelectedListener(requireContext()))
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
        binding.pieChartProblemTags.apply {

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
        profileViewModel.currentRatingColor.observe(viewLifecycleOwner) { rankColor ->
            binding.apply {
                firstName.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
                lastName.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
                handle.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
                rating.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
                rank.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
            }
        }
        profileViewModel.maxRatingColor.observe(viewLifecycleOwner) { rankColor ->
            binding.apply {
                maxRank.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
                maxRating.setTextColor(ContextCompat.getColor(requireContext(), rankColor))
            }
        }
    }


    private fun updateProfileCard(user: User) {
        binding.apply {
            contribution.text = "Contribution: " + user.contribution.toString()
            firstName.text = user.firstName
            lastName.text = user.lastName
            handle.text = "@" + user.handle
            rating.text = "Rating: " + user.rating.toString()
            rank.text = "Rank: " + user.rank
            maxRating.text = "Max Rating: " + user.maxRating.toString()
            maxRank.text = "Max Rank: " + user.maxRank
            city.text = "City: " + user.city
            country.text = "Country: " + user.country
            organization.text = "Organization: " + user.organization
            // TODO : registrating time show
            registrationTimeSeconds.visibility = View.GONE

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}