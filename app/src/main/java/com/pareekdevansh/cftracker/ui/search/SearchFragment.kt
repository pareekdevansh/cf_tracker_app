package com.pareekdevansh.cftracker.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pareekdevansh.cftracker.databinding.FragmentSearchBinding
import com.pareekdevansh.cftracker.repository.Repository


class SearchFragment : Fragment() {

    private val repository = Repository()
    private lateinit var searchViewModel : SearchViewModel
    private var _binding: FragmentSearchBinding
    ? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchViewModelProvider = SearchViewModelFactory(repository)
        searchViewModel =
            ViewModelProvider(this, searchViewModelProvider)[SearchViewModel::class.java]

        _binding = FragmentSearchBinding
            .inflate(inflater, container, false)

//        val textView: TextView = binding.textHome
//        searchViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query.let {
                            var userId = query?.filterNot { it.isWhitespace() }
                            val direction =
                                SearchFragmentDirections.actionNavigationSearchToUserSearchResultFragment(
                                    userId
                                )
                            findNavController().navigate(direction)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }


        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}