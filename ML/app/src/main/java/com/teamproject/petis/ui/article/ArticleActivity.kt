package com.teamproject.petis.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.teamproject.petis.adapter.ArticleAdapter
import com.teamproject.petis.databinding.ActivityArticleBinding
import com.teamproject.petis.ui.detail.DetailArticleActivity

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding
    private lateinit var articleAdapter: ArticleAdapter
    private val viewModel: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Articles"
        }

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, DetailArticleActivity::class.java).apply {
                putExtra(DetailArticleActivity.EXTRA_ARTICLE, article)
            }
            startActivity(intent)
        }

        binding.recyclerViewArticles.apply {
            layoutManager = GridLayoutManager(this@ArticleActivity, 1)
            adapter = articleAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.fetchArticles()
        viewModel.articles.observe(this) { articles ->
            articleAdapter.submitList(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}