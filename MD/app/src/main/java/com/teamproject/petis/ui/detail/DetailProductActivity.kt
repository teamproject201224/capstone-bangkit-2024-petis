package com.teamproject.petis.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.teamproject.petis.databinding.ActivityDetailProductBinding
import com.teamproject.petis.response.ProductResponseItem

class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductBinding

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getParcelableExtra<ProductResponseItem>(EXTRA_PRODUCT)
        product?.let { displayProductDetails(it) }

    }

    private fun displayProductDetails(product: ProductResponseItem) {
        binding.apply {
            Glide.with(this@DetailProductActivity)
                .load(product.image) .into(imgProduct)

            tvProductType.text = product.type
            tvProductName.text = product.name
            tvProductDescription.text = product.description
        }
    }
}
