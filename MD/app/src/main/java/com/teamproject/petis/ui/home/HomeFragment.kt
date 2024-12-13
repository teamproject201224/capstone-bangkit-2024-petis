package com.teamproject.petis.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.teamproject.petis.R
import com.teamproject.petis.adapter.ArticleAdapter
import com.teamproject.petis.adapter.HighlightAdapter
import com.teamproject.petis.databinding.FragmentHomeBinding
import com.teamproject.petis.model.Highlight
import com.teamproject.petis.ui.article.ArticleActivity
import com.teamproject.petis.ui.detail.DetailArticleActivity
import com.teamproject.petis.ui.pest.PestActivity
import com.teamproject.petis.ui.product.ProductActivity
import com.teamproject.petis.utils.SharedPreferencesManager
import com.teamproject.petis.utils.SharedViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var highlightAdapter: HighlightAdapter
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // Auto-scroll variables
    private val highlightHandler = Handler(Looper.getMainLooper())
    private var currentHighlightPosition = 0
    private val AUTO_SCROLL_INTERVAL = 4000L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSharedViewModel()
        // Inisialisasi ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        observeSharedViewModel()

        // Setup RecyclerView
        setupRecyclerView()

        // Observasi ViewModel
        observeViewModel()

        // Setup Button Clicks
        setupButtonClicks()

        // Setup View All Click
        setupViewAllClick()
        setupHighlightRecyclerView()
    }

    private fun setupHighlightRecyclerView() {
        highlightAdapter = HighlightAdapter()

        binding.highlightRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = highlightAdapter

            // Tambahkan PagerSnapHelper untuk efek scroll yang lebih halus
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }

        val highlights = listOf(
            Highlight(
                title = "Protect Your Plants, Achieve Optimal Yields!",
                description = "Safeguard your plants",
                icon = R.drawable.img_2,
                backgroundDrawable = R.drawable.rounded_background
            ),
            Highlight(
                title = "Instant Solutions for Your Problems",
                description = "Find the right way to tackle the pests attacking",
                icon = R.drawable.img_3,
                backgroundDrawable = R.drawable.rounded_background
            ),
            Highlight(
                title = "Detect Pests Quickly!",
                description = "Take a photo of your plant and identify pests in seconds.",
                icon = R.drawable.img_4,
                backgroundDrawable = R.drawable.rounded_background
            )
        )

        highlightAdapter.submitList(highlights)

        // Mulai auto-scroll
        startHighlightAutoScroll(highlights.size)
    }

    private fun startHighlightAutoScroll(itemCount: Int) {
        val scrollRunnable = object : Runnable {
            override fun run() {
                // Cek apakah fragment masih aktif
                if (isAdded && context != null) {
                    // Increment posisi, kembali ke 0 jika mencapai akhir
                    currentHighlightPosition = (currentHighlightPosition + 1 ) % itemCount

                    // Scroll ke posisi berikutnya
                    binding.highlightRecyclerView.smoothScrollToPosition(currentHighlightPosition)

                    // Jadwalkan scroll berikutnya
                    highlightHandler.postDelayed(this, AUTO_SCROLL_INTERVAL)
                }
            }
        }

        // Mulai scroll awal
        highlightHandler.postDelayed(scrollRunnable, AUTO_SCROLL_INTERVAL)
    }

    private fun setupHighlightInteraction() {
        binding.highlightRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Hentikan auto-scroll saat pengguna menyentuh
                        highlightHandler.removeCallbacksAndMessages(null)
                    }
                    MotionEvent.ACTION_UP -> {
                        // Lanjutkan auto-scroll setelah pengguna selesai menyentuh
                        startHighlightAutoScroll(highlightAdapter.itemCount)
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }


    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(requireContext(), DetailArticleActivity::class.java).apply {
                putExtra(DetailArticleActivity.EXTRA_ARTICLE, article)
            }
            startActivity(intent)
        }

        binding.recyclerViewHome.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = articleAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
        }

        homeViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.username.text = username
        }

        homeViewModel.greeting.observe(viewLifecycleOwner) { greeting ->
            binding.sapa.text = greeting
        }

        homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonClicks() {
        // Navigasi ke halaman Article
        binding.btnArticle.setOnClickListener {
            startActivity(Intent(requireContext(), ArticleActivity::class.java))
        }

        // Navigasi ke halaman Pest
        binding.btnPest.setOnClickListener {
            startActivity(Intent(requireContext(), PestActivity::class.java))
        }

        // Navigasi ke halaman Product
        binding.btnProduct.setOnClickListener {
            startActivity(Intent(requireContext(), ProductActivity::class.java))
        }
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(), ArticleActivity::class.java))
        }
    }

    private fun setupViewAllClick() {
        // Ketika "View all" diklik, pindah ke ArticleActivity
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(), ArticleActivity::class.java))
        }
    }

    private fun observeSharedViewModel() {
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.username.observe(viewLifecycleOwner) { newUsername ->
            binding.username.text = newUsername
            // Opsional: Update di SharedPreferences
            sharedPreferencesManager.saveUserName(newUsername)
        }
    }

    companion object {
        // Metode factory untuk membuat instance baru dari fragment
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}