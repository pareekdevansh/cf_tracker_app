package com.pareekdevansh.cftracker.ui.contest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pareekdevansh.cftracker.adapter.ContestAdapter
import com.pareekdevansh.cftracker.api.CFApi
import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.databinding.FragmentContestBinding
import com.pareekdevansh.cftracker.models.Contest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch

class ContestFragment : Fragment() {

    lateinit var rv: RecyclerView
    var contests: MutableList<Contest> = mutableListOf()
    lateinit var contestAdapter: ContestAdapter

    private var _binding: FragmentContestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contestViewModel =
            ViewModelProvider(this)[ContestViewModel::class.java]

        _binding = FragmentContestBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        contestViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = binding.rvContests
        setupRV(contests)
        getContestList()
        contests?.add(Contest(2, true, 122, "Subhadip", "Hehahaha", 12, 123, "Nice"))
        contests?.add(Contest(2, true, 122, "Subhadip", "Hehahaha", 12, 123, "Nice"))
        contests?.add(Contest(2, true, 122, "Subhadip", "Hehahaha", 12, 123, "Nice"))
        contestAdapter.notifyDataSetChanged()
    }

    private fun getContestList() {

//        CoroutineScope(Dispatchers.IO).launch{
//            val minterface : CFApi? = RetrofitInstance.api()
//            val call = minterface?.getContests()
//            call?.enqueue(object : Callback<ContestResponseModel> {
//                override fun onResponse(
//                    call: Call<ContestResponseModel>,
//                    response: Response<ContestResponseModel>
//                ) {
//                    if(response.isSuccessful && response.body() != null ){
//                        if(response.body()!!.contest.isEmpty())
//                        for( c : Contest in response.body()!!.contest){
//                            contests?.add(Contest(c.durationSeconds,c.frozen,c.id,c.name,c.phase,c.relativeTimeSeconds,c.startTimeSeconds,c.type))
//                        }
//                        contestAdapter.notifyDataSetChanged()
//                    }
//                }
//
//                override fun onFailure(call: Call<ContestResponseModel>, t: Throwable) {
//
//                }
//            })
        GlobalScope.launch {

            try {
                Log.d("contest", "making network call ")
                val minterface: CFApi? = RetrofitInstance.api()
                val response = minterface?.getContests()
                if (response!!.isSuccessful && response.body() != null) {

                    if (response.body()!!.contest.isEmpty())
                        for (c: Contest in response.body()!!.contest) {
                            contests.add(c)
//                            contests?.add(Contest(c.durationSeconds,c.frozen,c.id,c.name,c.phase,c.relativeTimeSeconds,c.startTimeSeconds,c.type))
                        }
                    contestAdapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Log.d("contest4", e.message.toString())
            }
        }
    }

    private fun setupRV(list: MutableList<Contest>) {

        contestAdapter = ContestAdapter(list)
        contests.let {
            rv.apply {

                adapter = contestAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}