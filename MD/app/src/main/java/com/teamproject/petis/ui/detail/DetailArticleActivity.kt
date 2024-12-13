package com.teamproject.petis.ui.detail

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ActivityDetailArticleBinding
import com.teamproject.petis.response.ArticleResponse
import java.net.MalformedURLException
import java.net.URL

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding
    private var retryCount = 0
    private val MAX_RETRIES = 3

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<ArticleResponse>(EXTRA_ARTICLE)

        if (article != null) {
            displayArticleDetails(article)
        } else {
            Toast.makeText(this, "Article not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupToolbar()
    }

    private fun displayArticleDetails(article: ArticleResponse) {
        try {
            binding.textViewTitle.text = article.displayTitle

            Glide.with(this)
                .load(article.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.imageViewArticle)

            binding.textViewContent.text = article.content

            setupWebView(article.link)
        } catch (e: Exception) {
            Log.e("DetailArticleActivity", "Error displaying article details", e)
            Toast.makeText(this, "Error loading article details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupWebView(url: String) {
        // Cek validitas URL
        if (url.isEmpty() || !isValidUrl(url)) {
            showErrorMessage("Invalid URL")
            return
        }

        // Cek koneksi internet
        if (!isNetworkAvailable()) {
            showNoInternetDialog {
                // Lambda untuk retry
                setupWebView(url)
            }
            return
        }

        binding.webViewArticle.apply {
            // Konfigurasi WebView Settings
            configureWebViewSettings()

            // Setup WebViewClient
            webViewClient = createWebViewClient()

            // Setup WebChromeClient
            webChromeClient = createWebChromeClient()

            // Mulai memuat URL
            loadUrlWithRetry(url)
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            url.startsWith("http://") || url.startsWith("https://")
        } catch (e: MalformedURLException) {
            false
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun WebView.configureWebViewSettings() {
        settings.apply {
            // Pengaturan dasar
            javaScriptEnabled = true
            domStorageEnabled = true

            // Pengaturan tampilan
            loadWithOverviewMode = true
            useWideViewPort = true

            // Pengaturan zoom
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            // Pengaturan cache
            cacheMode = WebSettings.LOAD_NO_CACHE

            // Pengaturan konten campuran
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // User agent kustom
            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"

            // Rendering
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }
    }

    private fun createWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                // Tampilkan loading
                binding.progressBar.visibility = View.VISIBLE

                // Timeout handler
                view?.postDelayed({
                    if (view.progress < 100) {
                        view.stopLoading()
                        handleLoadingTimeout()
                    }
                }, 15000) // 15 detik timeout
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Sembunyikan loading
                binding.progressBar.visibility = View.GONE

                // Reset retry count
                retryCount = 0
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                // Log error
                Log.e("WebView", "Error Loading: ${request?.url}")
                Log.e("WebView", "Error Description: ${error?.description}")

                // Tampilkan pesan error
                showErrorMessage(error?.description?.toString() ?: "Unknown error")
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let {
                    view?.loadUrl(it.toString())
                }
                return true
            }
        }
    }

    private fun createWebChromeClient(): WebChromeClient {
        return object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Update progress bar
                binding.progressBar.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                // Opsional: Update judul
                // supportActionBar?.title = title
            }
        }
    }

    private fun WebView.loadUrlWithRetry(url: String) {
        try {
            // Tambahkan header kustom jika diperlukan
            loadUrl(url, mapOf(
                "User-Agent" to settings.userAgentString
            ))
        } catch (e: Exception) {
            if (retryCount < MAX_RETRIES) {
                retryCount++
                // Retry dengan penundaan eksponensial
                postDelayed({
                    loadUrlWithRetry(url)
                }, 2000L * retryCount)
            } else {
                showErrorMessage("Failed to load after $MAX_RETRIES attempts")
            }
        }
    }

    private fun handleLoadingTimeout() {
        showErrorMessage("Loading timeout. Check your internet connection.")
    }

    private fun showErrorMessage(message: String) {
        // Tampilkan pesan error dengan Snackbar atau Toast
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply {
            setAction("Retry") {
                // Logika retry setupWebView(/* url */)
            }
            show()
        }
    }

    private fun showNoInternetDialog(retryAction: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("Retry") { _, _ ->
                // Jalankan aksi retry yang diteruskan
                retryAction()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Detail Article"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (binding.webViewArticle.canGoBack()) {
            binding.webViewArticle.goBack()
        } else {
            super.onBackPressed()
        }
    }
}