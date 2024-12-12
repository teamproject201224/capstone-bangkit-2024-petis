package com.teamproject.petis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ItemProductBinding
import com.teamproject.petis.response.ProductResponseItem

class ProductAdapter(
    private val onItemClick: (ProductResponseItem) -> Unit
) : ListAdapter<ProductResponseItem, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onItemClick: (ProductResponseItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductResponseItem) {
            with(binding) {
                // Load gambar dengan Glide dan error handling
                Glide.with(itemView.context)
                    .load(product.image)
                    .placeholder(R.drawable.placeholder_image) // Tambahkan placeholder
                    .error(R.drawable.error_image) // Tambahkan error image
                    .centerCrop()
                    .into(imgProduct)

                tvProductType.text = product.type
                tvProductName.text = product.name
                tvProductDescription.text = product.description

                root.setOnClickListener { onItemClick(product) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductResponseItem>() {
        override fun areItemsTheSame(
            oldItem: ProductResponseItem,
            newItem: ProductResponseItem
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: ProductResponseItem,
            newItem: ProductResponseItem
        ): Boolean = oldItem ==newItem
    }
}
