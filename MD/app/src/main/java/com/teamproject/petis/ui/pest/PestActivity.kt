package com.teamproject.petis.ui.pest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamproject.petis.adapter.PestAdapter
import com.teamproject.petis.api.ApiClient
import com.teamproject.petis.databinding.ActivityPestBinding
import com.teamproject.petis.ui.detail.DetailPestActivity
import com.teamproject.petis.utils.ViewModelFactory

class PestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPestBinding
    private lateinit var pestViewModel: PestViewModel
    private lateinit var pestAdapter: PestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gunakan supportActionBar atau toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Pest"
        }

        setupViewModel()
        setupRecyclerView()
        setupSwipeRefreshLayout()
        observeViewModel()

        pestViewModel.fetchPestData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun setupViewModel() {
        val apiService = ApiClient.apiService
        val pestRepository = PestRepository(apiService)
        val viewModelFactory = ViewModelFactory(pestRepository)
        pestViewModel = ViewModelProvider(this, viewModelFactory)[PestViewModel::class.java]
    }

    private fun setupRecyclerView() {
        pestAdapter = PestAdapter { pestItem ->
            val intent = Intent(this, DetailPestActivity::class.java).apply {
                putExtra(DetailPestActivity.EXTRA_PEST, pestItem)
            }
            startActivity(intent)
        }
        binding.pestRecyclerView.apply {
            layoutManager = GridLayoutManager(this@PestActivity, 2)
            adapter = pestAdapter
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            pestViewModel.refreshPestData()
        }
    }

    private fun observeViewModel() {
        pestViewModel.pestList.observe(this) { pestList ->
            if (pestList.isNotEmpty()) {
                pestAdapter.submitList(pestList)
                binding.emptyStateView.visibility = View.GONE
                binding.pestRecyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyStateView.visibility = View.VISIBLE
                binding.pestRecyclerView.visibility = View.GONE
            }
        }

        pestViewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        pestViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
