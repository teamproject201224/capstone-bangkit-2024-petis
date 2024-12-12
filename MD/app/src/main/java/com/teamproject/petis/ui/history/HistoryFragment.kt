package com.teamproject.petis.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamproject.petis.adapter.HistoryAdapter
import com.teamproject.petis.databinding.FragmentHistoryBinding
import com.teamproject.petis.db.HistoryDatabase
import com.teamproject.petis.db.HistoryRepository

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { historyEntity ->
            historyViewModel.delete(historyEntity)
            Toast.makeText(context, "History deleted", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupViewModel() {
        val database = HistoryDatabase.getDatabase(requireContext().applicationContext)
        val repository = HistoryRepository(database.historyDao())
        val viewModelFactory = HistoryViewModelFactory(repository)

        historyViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[HistoryViewModel::class.java]

        historyViewModel.allHistory.observe(viewLifecycleOwner) { histories ->
            histories?.let {
                historyAdapter.submitList(it)
                binding.emptyStateLayout.visibility =
                    if (it.isEmpty()) View.VISIBLE
                    else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}