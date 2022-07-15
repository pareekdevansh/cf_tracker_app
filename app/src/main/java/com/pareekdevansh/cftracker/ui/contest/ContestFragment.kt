package com.pareekdevansh.cftracker.ui.contest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.adapter.ContestAdapter
import com.pareekdevansh.cftracker.databinding.FragmentContestBinding
import com.pareekdevansh.cftracker.repository.Repository


class ContestFragment : Fragment() {

    private val TAG = "#ContestFragment"
    private lateinit var rv: RecyclerView
    lateinit var contestAdapter: ContestAdapter
    lateinit var contestViewModel: ContestViewModel

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
        rv = binding.rvContests
        contestViewModel.getContest()
        contestViewModel.contestResponse.observe(viewLifecycleOwner) { response ->
            Log.d(TAG, "response -> $response")
            Log.d(TAG, response.body()?.status.toString())
            Log.d(TAG, response.body()?.contest.toString())
        }


    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}