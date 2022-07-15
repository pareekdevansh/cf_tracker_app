package com.pareekdevansh.cftracker.ui.contest

import android.os.Bundle
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


class ContestFragment : Fragment() {

    private val TAG = "#ContestFragment"
    private lateinit var recyclerView: RecyclerView
    lateinit var contestViewModel: ContestViewModel
    lateinit var contestAdapter: ContestAdapter
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
        recyclerView = binding.rvContests
        setupRecyclerView()

        contestViewModel.getContest()
        contestViewModel.contestResponse.observe(viewLifecycleOwner) { response ->
            if(response.isSuccessful){
                // update the list showing contests
                contestAdapter.differ.submitList(response.body()?.contest)
            }
            else{
                // show error message
                Toast.makeText(requireContext() , "Error Occured", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun setupRecyclerView() {
        contestAdapter = ContestAdapter()
        recyclerView.apply {
            adapter = contestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}