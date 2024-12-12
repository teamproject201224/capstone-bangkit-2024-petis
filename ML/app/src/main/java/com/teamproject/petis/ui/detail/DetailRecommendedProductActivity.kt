package com.teamproject.petis.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.api.RecommendedProductResponse
import com.teamproject.petis.databinding.ActivityDetailRecommendedProductBinding

class DetailRecommendedProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecommendedProductBinding

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecommendedProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil produk dari intent
        val product = intent.getParcelableExtra<RecommendedProductResponse>(EXTRA_PRODUCT)

        // Tampilkan data produk
        product?.let { setupProductDetails(it) }

        // Atur listener untuk tombol kembali
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupProductDetails(product: RecommendedProductResponse) {
        // Gambar produk
        Glide.with(this)
            .load(product.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(binding.productImageView)

        // Nama produk
        binding.productNameTextView.text = product.name

        // Tipe produk
        binding.typeTextView.text = product.type

        // Deskripsi produk
        binding.descriptionTextView.text = product.description

        // Tombol link produk
        binding.openLinkButton.apply {
            visibility = if (product.link.isNotBlank()) View.VISIBLE else View.GONE
            setOnClickListener { openProductLink(product.link) }
        }
    }

    private fun openProductLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        } catch (e: Exception) {
            // Tangani jika tidak ada aplikasi yang bisa membuka link
        }
    }
}