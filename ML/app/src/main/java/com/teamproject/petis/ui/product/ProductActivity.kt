package com.teamproject.petis.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamproject.petis.adapter.ProductAdapter
import com.teamproject.petis.databinding.ActivityProductBinding
import com.teamproject.petis.response.ProductResponseItem
import com.teamproject.petis.ui.detail.DetailProductActivity

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private lateinit var productAdapter: ProductAdapter
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gunakan supportActionBar atau toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Products"
        }

        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()

        viewModel.fetchProducts()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            navigateToProductDetail(product)
        }
        binding.productRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ProductActivity, 2)
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.productRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchProducts()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun navigateToProductDetail(product: ProductResponseItem) {
        val intent = Intent(this, DetailProductActivity::class.java).apply {
            putExtra(DetailProductActivity.EXTRA_PRODUCT, product)
        }
        startActivity(intent)
    }
}
