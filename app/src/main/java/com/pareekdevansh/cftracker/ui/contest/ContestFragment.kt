package com.pareekdevansh.cftracker.ui.contest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.R
import com.pareekdevansh.cftracker.adapter.ContestAdapter
import com.pareekdevansh.cftracker.databinding.FragmentContestBinding
import com.pareekdevansh.cftracker.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlin.math.abs


class ContestFragment : Fragment() {

    private val TAG = "#ContestFragment"
    lateinit var contestViewModel: ContestViewModel
    lateinit var liveContestAdapter: ContestAdapter
    lateinit var recentContestAdapter: ContestAdapter
    lateinit var upcomingContestAdapter: ContestAdapter
    lateinit var rvLiveContest: RecyclerView
    lateinit var rvRecentContest: RecyclerView
    lateinit var rvUpcomingContest: RecyclerView
    private var _binding: FragmentContestBinding? = null
    private var liveContestCardExpanded = true
    private var recentContestCardExpanded = false
    private var upcomingContestCardExpanded = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val contestsViewModelFactory = ContestViewModelFactory(repository)
        contestViewModel =
            ViewModelProvider(this, contestsViewModelFactory)[ContestViewModel::class.java]

        _binding = FragmentContestBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvLiveContest = binding.rvLiveContest
        rvRecentContest = binding.rvRecentContest
        rvUpcomingContest = binding.rvUpcomingContest

        setupRecyclerView()

        contestViewModel.getContest()
        contestViewModel.contestResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                // update live contests
                binding.apply {
                    loadingAnimation.pauseAnimation()
                    loadingAnimation.visibility = View.GONE
                    LiveContestCard.visibility = View.VISIBLE
                    recentContestCard.visibility = View.VISIBLE
                    upComingContestCard.visibility = View.VISIBLE
                }
                val liveContest = response.body()?.contest?.filter { it.phase == "CODING" }
                if (liveContest == null || liveContest.isEmpty()) {
                    binding.noLiveContestAnimationLL.visibility = View.VISIBLE
                    binding.rvLiveContest.visibility = View.GONE
                } else {
                    binding.noLiveContestAnimationLL.visibility = View.GONE
                    binding.rvLiveContest.visibility = View.VISIBLE
                    liveContestAdapter.differ.submitList(liveContest)
                }

                // update recent contests
                val recentContests =
                    response.body()?.contest?.filter { (it.phase == "FINISHED" || it.phase == "SYSTEM_TEST" || it.phase == "PENDING_SYSTEM_TEST") && (it.relativeTimeSeconds?.let { time -> time <= 24 * 7 * 60 * 60 } == true) }
                if (recentContests == null) {
                    //TODO: show there are no recent contests
                } else {
                    recentContestAdapter.differ.submitList(recentContests)
                }

                // update upcoming contests
                val upcomingContests = response.body()?.contest?.filter {
                    it.phase == "BEFORE" && (it.relativeTimeSeconds?.let { time ->
                        abs(time) <= 24 * 7 * 60 * 60
                    } == true)
                }
                if (upcomingContests == null) {
                    // TODO: no upcoming contests
                } else {
                    upcomingContestAdapter.differ.submitList(upcomingContests.asReversed())
                }
            } else {
                showToast(response.message())
            }
        }

        contestViewModel.ratingChangeResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Log.d("blablabla", response.toString())
                Log.d("blablabla", response.body().toString())
            } else {
                showToast(response.message())
            }

        }

        binding.btnExpandLiveContest.setOnClickListener {
            var imageId: Int
            var viewVisibility: Int
            var anim: Int
            if (liveContestCardExpanded) {
                // make the recycler view visibility to Gone
                viewVisibility = View.GONE
                // change the button icon to expand more
                imageId = R.drawable.baseline_expand_more_24
                anim = R.anim.slide_up_anim
            } else {
                // make recycler view visible
                viewVisibility = View.VISIBLE
                // change icon sign to expand less
                imageId = R.drawable.baseline_expand_less_24
                anim = R.anim.slide_down_anim
            }
            binding.noLiveContestAnimationLL.visibility = viewVisibility
            binding.rvLiveContest.apply {
                visibility = viewVisibility
                startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
            }
            liveContestCardExpanded = !liveContestCardExpanded
            binding.btnExpandLiveContest.setImageDrawable(resources.getDrawable(imageId))
        }
        binding.btnExpandUpcomingContest.setOnClickListener {
            var imageId: Int
            var viewVisibility: Int
            var anim: Int
            if (upcomingContestCardExpanded) {
                // make the recycler view visibility to Gone
                viewVisibility = View.GONE
                // change the button icon to expand more
                imageId = R.drawable.baseline_expand_more_24
                anim = R.anim.slide_up_anim
            } else {
                // make recycler view visible
                viewVisibility = View.VISIBLE
                // change icon sign to expand less
                imageId = R.drawable.baseline_expand_less_24
                anim = R.anim.slide_down_anim
            }
            binding.rvUpcomingContest.apply {
                visibility = viewVisibility
                startAnimation(AnimationUtils.loadAnimation(requireContext(), anim))
            }
            upcomingContestCardExpanded = !upcomingContestCardExpanded
            binding.btnExpandUpcomingContest.setImageDrawable(resources.getDrawable(imageId))
        }

        binding.btnExpandRecentContest.setOnClickListener {
            var imageId: Int
            var anim1: Int
            var viewVisibility: Int
            if (recentContestCardExpanded) {
                // make the recycler view visibility to Gone
                viewVisibility = View.GONE
                // change the button icon to expand more
                imageId = R.drawable.baseline_expand_more_24
                anim1 = R.anim.slide_up_anim

            } else {
                // make recycler view visible
                viewVisibility = View.VISIBLE
                // change icon sign to expand less
                imageId = R.drawable.baseline_expand_less_24
                anim1 = R.anim.slide_down_anim
            }
            binding.rvRecentContest.apply {
                visibility = viewVisibility
                startAnimation(AnimationUtils.loadAnimation(requireContext(), anim1))
            }
            recentContestCardExpanded = !recentContestCardExpanded
            binding.btnExpandRecentContest.setImageDrawable(resources.getDrawable(imageId))
        }


    }

    private fun showToast(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerView() {
        setupLiveContests()
        setupRecentContests()
        setupUpcomingContests()
    }

    private fun setupUpcomingContests() {
        upcomingContestAdapter = ContestAdapter()
        binding.rvUpcomingContest.apply {
            adapter = upcomingContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.rvUpcomingContest.visibility = View.GONE
    }

    private fun setupRecentContests() {
        recentContestAdapter = ContestAdapter()
        binding.rvRecentContest.apply {
            adapter = recentContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.rvRecentContest.visibility = View.GONE
    }

    private fun setupLiveContests() {
        liveContestAdapter = ContestAdapter()
        binding.rvLiveContest.apply {
            adapter = liveContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.rvLiveContest.visibility = View.GONE

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}