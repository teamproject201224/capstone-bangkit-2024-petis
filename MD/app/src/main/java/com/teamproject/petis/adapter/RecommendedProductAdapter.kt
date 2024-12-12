package com.teamproject.petis.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.api.RecommendedProductResponse

class RecommendedProductAdapter(
    private var products: List<RecommendedProductResponse>,
    private val onItemClickListener: (RecommendedProductResponse) -> Unit
) : RecyclerView.Adapter<RecommendedProductAdapter.ProductViewHolder>() {

    fun updateProducts(newProducts: List<RecommendedProductResponse>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductType: TextView = itemView.findViewById(R.id.tvProductType)
        private val btnProductLink: Button = itemView.findViewById(R.id.btnProductLink)

        fun bind(product: RecommendedProductResponse) {
            // Load image with Glide
            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivProductImage)

            tvProductName.text = product.name
            tvProductType.text = product.type

            btnProductLink.apply {
                visibility = if (product.link.isNotBlank()) View.VISIBLE else View.GONE
                setOnClickListener {
                    // Buka link produk
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.link))
                    itemView.context.startActivity(intent)
                }
            }

            itemView.setOnClickListener {
                onItemClickListener(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}