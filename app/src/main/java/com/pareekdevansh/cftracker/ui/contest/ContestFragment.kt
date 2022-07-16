package com.pareekdevansh.cftracker.ui.contest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.adapter.ContestAdapter
import com.pareekdevansh.cftracker.databinding.FragmentContestBinding
import com.pareekdevansh.cftracker.repository.Repository
import kotlin.math.abs


class ContestFragment : Fragment() {

    private val TAG = "#ContestFragment"
    lateinit var contestViewModel: ContestViewModel
    lateinit var liveContestAdapter: ContestAdapter
    lateinit var recentContestAdapter: ContestAdapter
    lateinit var upcomingContestAdapter: ContestAdapter
    lateinit var rvLiveContest : RecyclerView
    lateinit var rvRecentContest : RecyclerView
    lateinit var rvUpcomingContest : RecyclerView
    private var _binding: FragmentContestBinding? = null

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
            if(response.isSuccessful){
                // update live contests

                val liveContest = response.body()?.contest?.filter { it.phase == "CODING" }
                if(liveContest == null ){
                    // no live contest now
                }
                else{
                    liveContestAdapter.differ.submitList(liveContest)
                }

                // update recent contests
                val recentContests = response.body()?.contest?.filter {( it.phase == "FINISHED" || it.phase == "SYSTEM_TEST" || it.phase == "PENDING_SYSTEM_TEST") && (it.relativeTimeSeconds?.let { time -> time <= 24 * 7 * 60 * 60 } == true)  }
                if(recentContests == null )
                {
                    // there are no recent contests
                }
                else{
                    recentContestAdapter.differ.submitList(recentContests)
                }

                // update upcoming contests
                val upcomingContests = response.body()?.contest?.filter { it.phase == "BEFORE" && (it.relativeTimeSeconds?.let { time -> abs(time) <= 24 * 7 * 60 * 60 } == true) }
                if(upcomingContests == null ){
                    // no upcoming contests
                }
                else{
                    upcomingContestAdapter.differ.submitList(upcomingContests.asReversed())
                }
            }
            else{
                showToast(response.message())
            }
        }

        contestViewModel.ratingChangeResponse.observe(viewLifecycleOwner){ response ->
            if(response.isSuccessful){
                Log.d("blablabla" , response.toString())
                Log.d("blablabla", response.body().toString())
            }
            else{
                showToast(response.message())
            }

        }


    }

    private fun showToast(error: String) {
        Toast.makeText(requireContext()  , error , Toast.LENGTH_LONG ).show()
    }

    private fun setupRecyclerView() {
        showLiveContest()
        showRecentContest()
        showUpcomingContest()
    }

    private fun showUpcomingContest() {
        upcomingContestAdapter = ContestAdapter()
        binding.rvUpcomingContest.apply {
            adapter = upcomingContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showRecentContest() {
        recentContestAdapter = ContestAdapter()
        binding.rvRecentContest.apply {
            adapter = recentContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showLiveContest() {
        liveContestAdapter = ContestAdapter()
        binding.rvLiveContest.apply {
            adapter = liveContestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}